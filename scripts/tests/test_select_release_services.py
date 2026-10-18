import unittest

from scripts.select_release_services import changed_release_services


POM_TEMPLATE = """<project>
  <properties>
    <auth.version>{auth}</auth.version>
    <users.version>{users}</users.version>
    <appointments.version>{appointments}</appointments.version>
    <api.version>{api}</api.version>
    <frontend.version>{frontend}</frontend.version>
  </properties>
</project>
"""

MANIFEST_TEMPLATE = """services:
  auth:
    image: example/auth
    tag: {auth}
  users:
    image: example/users
    tag: {users}
  appointments:
    image: example/appointments
    tag: {appointments}
  api:
    image: example/api
    tag: {api}
  frontend:
    image: example/frontend
    tag: {frontend}
"""


class SelectReleaseServicesTests(unittest.TestCase):
    def test_only_service_with_changed_release_version_is_selected(self):
        base_values = {
            "auth": "1.0.0",
            "users": "1.0.0",
            "appointments": "1.0.0",
            "api": "1.0.0",
            "frontend": "1.0.0",
        }
        current_values = {**base_values, "frontend": "1.0.1"}
        selected = changed_release_services(
            POM_TEMPLATE.format(**base_values),
            POM_TEMPLATE.format(**current_values),
            MANIFEST_TEMPLATE.format(**base_values),
            MANIFEST_TEMPLATE.format(**current_values),
        )
        self.assertEqual(selected, ["frontend"])

    def test_no_version_changes_select_no_services(self):
        values = {
            "auth": "1.0.0",
            "users": "1.0.0",
            "appointments": "1.0.0",
            "api": "1.0.0",
            "frontend": "1.0.0",
        }
        pom = POM_TEMPLATE.format(**values)
        manifest = MANIFEST_TEMPLATE.format(**values)
        self.assertEqual(changed_release_services(pom, pom, manifest, manifest), [])


if __name__ == "__main__":
    unittest.main()