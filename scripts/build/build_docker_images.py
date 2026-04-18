from abstract_builders.abstract_module_builder import AbstractModuleBuilder, BuildProcessResponse

import os
import subprocess
import sys

class DockerImageBuilder(AbstractModuleBuilder):
    BASE_DIR = os.path.dirname(os.path.dirname(os.path.dirname(__file__)))

    def __init__(self, init_modules=None):
        super().__init__(["api", "appointments", "auth", "diagnoses", "users", "frontend"], init_modules)

    def run_build_module_command(self, path):
        module_name = os.path.basename(path)

        cmd = ["docker", "buildx", "build", "--platform", "linux/amd64", "-t", f"todorpopov02/{module_name}-service:latest", "--push", "."]

        self._logger.info(f"Building Docker image for module '{module_name}' in '{path}")
        self._logger.info(f"Executing command: {cmd}")

        try:
            subprocess.run(
                cmd,
                cwd=path,
                check=True,
                stdout=subprocess.PIPE,
                stderr=subprocess.PIPE,
                text=True
            )

            return BuildProcessResponse(True, module_name, None)
        except subprocess.CalledProcessError as e:
            return BuildProcessResponse(False, module_name, e.stderr)

    def main(self):
        modules = self._get_modules()

        success_count = 0
        fail_count = 0

        for module in modules:
            module_path = os.path.join(self.BASE_DIR, f"{module}")
            print("PATH: " + module_path)

            result = self.run_build_module_command(module_path)

            if not result.is_successful:
                self._logger.error(f"Failed to build Docker image '{result.module_name}: {result.errors}")
                fail_count += 1
            else:
                self._logger.info(f"Docker image for module '{result.module_name}' has been built successfully!")
                success_count += 1

        self._logger.info(f"Successful builds: {success_count} | Failed builds: {fail_count}")
        return self.SUCCESS_STATUS


if __name__ == "__main__":
    obj = DockerImageBuilder()
