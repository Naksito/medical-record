import logging
import argparse
from abc import ABC

class BuildProcessResponse:
    def __init__(self, is_successful, module_name, errors):
        self.is_successful = is_successful
        self.module_name = module_name
        self.errors = errors

class AbstractModuleBuilder(ABC):
    SUCCESS_STATUS = 0
    ERROR_STATUS = 1

    def __init__(self, correct_modules, init_modules=None):
        logging.basicConfig(
            level=logging.INFO,
            format='%(asctime)s - %(levelname)s - %(message)s',
            datefmt='%Y-%m-%d %H:%M:%S'
        )

        self._logger = logging.getLogger(__name__)
        self.correct_modules = correct_modules
        self.init_modules = self._validate_modules(init_modules) if init_modules is not None else None

        self._logger.info(f"Init modules: {self.init_modules}")

    def _validate_modules(self, modules):
        valid_modules = []

        for module in modules:
            if module in self.correct_modules:
                valid_modules.append(module)
            else:
                self._logger.info(f"Module '{module}' is not correct!")

        return valid_modules

    def _get_modules(self):
        if self.init_modules is not None:
            return self.init_modules

        parser = argparse.ArgumentParser(description="Modules builder parser")
        parser.add_argument("--modules", nargs='+', help="Specific modules to build. Separate modules by a single space!")

        args = parser.parse_args()

        if args.modules is None:
            return self.correct_modules

        self._logger.info(f"Modules passed: {args.modules}")

        return self._validate_modules(args.modules)

    def run_build_module_command(self, path):
        raise NotImplementedError("This method is not implemented in the abstract class. Please override it!")

    def main(self):
        raise NotImplementedError("This method is not implemented in the abstract class. Please override it!")
