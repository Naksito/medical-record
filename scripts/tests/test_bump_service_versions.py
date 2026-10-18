import subprocess
import sys
import tempfile
import unittest
from pathlib import Path

from scripts.bump_service_versions import (
    changed_services,
    choose_next_version,
    get_property,
    set_manifest_tag,
    set_property,
)


class BumpServiceVersionsTests(unittest.TestCase):
    def test_patch_version_increments(self):
        self.assertEqual(choose_next_version("1.2.9", "1.2.9"), "1.2.10")

    def test_higher_manual_version_is_preserved(self):
        self.assertEqual(choose_next_version("1.2.9", "2.0.0"), "2.0.0")

    def test_higher_minor_manual_version_is_preserved(self):
        self.assertEqual(choose_next_version("1.2.9", "1.3.0"), "1.3.0")

    def test_lower_manual_version_is_rejected(self):
        with self.assertRaises(ValueError):
            choose_next_version("1.2.9", "1.2.8")

    def test_invalid_semver_is_rejected(self):
        with self.assertRaises(ValueError):
            choose_next_version("1.2", "1.2")

    def test_changed_service_paths_are_detected(self):
        self.assertEqual(
            changed_services(["auth/src/Main.java", "frontend/src/main.ts", "README.md"]),
            ["auth", "frontend"],
        )

    def test_root_pom_property_can_be_read_and_updated(self):
        pom = "<properties>\n    <auth.version>1.2.3</auth.version>\n</properties>\n"
        updated = set_property(pom, "auth.version", "1.2.4")
        self.assertEqual(get_property(updated, "auth.version"), "1.2.4")

    def test_manifest_tag_is_updated_for_selected_service(self):
        manifest = (
            "services:\n"
            "  auth:\n"
            "    image: example/auth\n"
            "    tag: 1.0.0\n"
            "  users:\n"
            "    image: example/users\n"
            "    tag: 4.5.6\n"
        )
        updated = set_manifest_tag(manifest, "auth", "1.0.1")
        self.assertIn("    tag: 1.0.1\n", updated)
        self.assertIn("    tag: 4.5.6\n", updated)

    def test_script_bumps_only_changed_service_and_updates_manifest(self):
        with tempfile.TemporaryDirectory() as temp_dir:
            repo = Path(temp_dir)
            (repo / "auth" / "src").mkdir(parents=True)
            (repo / "auth" / "src" / "Main.java").write_text("class Main {}\n")
            (repo / "k8s").mkdir()
            (repo / "pom.xml").write_text(
                "<project>\n  <properties>\n"
                "    <auth.version>1.0.0</auth.version>\n"
                "    <users.version>1.0.0</users.version>\n"
                "    <appointments.version>1.0.0</appointments.version>\n"
                "    <api.version>1.0.0</api.version>\n"
                "    <frontend.version>1.0.0</frontend.version>\n"
                "  </properties>\n</project>\n"
            )
            (repo / "k8s" / "versions.yaml").write_text(
                "services:\n"
                "  auth:\n    image: example/auth\n    tag: 1.0.0\n"
                "  users:\n    image: example/users\n    tag: 1.0.0\n"
                "  appointments:\n    image: example/appointments\n    tag: 1.0.0\n"
                "  api:\n    image: example/api\n    tag: 1.0.0\n"
                "  frontend:\n    image: example/frontend\n    tag: 1.0.0\n"
            )
            self.run_git(repo, "init", "-q")
            self.run_git(repo, "config", "user.email", "demo@example.com")
            self.run_git(repo, "config", "user.name", "Demo")
            self.run_git(repo, "add", ".")
            self.run_git(repo, "commit", "-qm", "baseline")
            base_ref = self.run_git(repo, "rev-parse", "HEAD").stdout.decode().strip()

            (repo / "auth" / "src" / "Main.java").write_text("class Main { int v = 2; }\n")
            self.run_git(repo, "add", ".")
            self.run_git(repo, "commit", "-qm", "change auth service")
            result = subprocess.run(
                [
                    sys.executable,
                    str(Path(__file__).resolve().parents[1] / "bump_service_versions.py"),
                    "--base-ref",
                    base_ref,
                    "--repo-root",
                    str(repo),
                ],
                check=False,
                capture_output=True,
                text=True,
            )
            self.assertEqual(result.returncode, 0, result.stderr)

            pom = (repo / "pom.xml").read_text()
            manifest = (repo / "k8s" / "versions.yaml").read_text()
            self.assertIn("<auth.version>1.0.1</auth.version>", pom)
            self.assertIn("<users.version>1.0.0</users.version>", pom)
            self.assertIn("  auth:\n    image: example/auth\n    tag: 1.0.1", manifest)
            self.assertIn("  users:\n    image: example/users\n    tag: 1.0.0", manifest)

    @staticmethod
    def run_git(repo, *args):
        return subprocess.run(
            ["git", "-C", str(repo), *args], check=True, capture_output=True
        )


if __name__ == "__main__":
    unittest.main()