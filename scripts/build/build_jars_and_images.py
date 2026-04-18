import argparse
import sys

from build_docker_images import DockerImageBuilder
from build_jar_files import JarFileBuilder


def _validate_modules(correct_modules, modules):
    valid_modules = []

    for module in modules:
        if module in correct_modules:
            valid_modules.append(module)
        else:
            print(f"Module '{module}' is not correct!")

    return valid_modules

def _get_modules(correct_modules):
    parser = argparse.ArgumentParser(description="Modules builder parser")
    parser.add_argument("--modules", nargs='+', help="Specific modules to build. Separate modules by a single space!")

    args = parser.parse_args()

    if args.modules is None:
        return correct_modules

    print(f"Modules passed: {args.modules}")

    return _validate_modules(correct_modules, args.modules)

if __name__ == "__main__":
    correct_modules = ["api", "appointments", "auth", "diagnoses", "users", "frontend"]
    modules = _get_modules(correct_modules)

    jars = JarFileBuilder(modules).main()

    if jars != 0:
        sys.exit(1)

    images = DockerImageBuilder(modules).main()

    if images != 0:
        sys.exit(1)
    else:
        sys.exit(0)
