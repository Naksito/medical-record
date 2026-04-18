# Build Scripts
This directory hosts all build scripts. These scripts are used for building different parts of the system, so that
everything can be conveniently bootstraped through only a few commands.

Scripts are designed for both CLI usage, as well as programatically importing and running the different classes.

## Script - `build_jar_files.py`
This script is used to build the jar files for the different Maven submodules.

To use the script, you need to have Python 3, Maven and a JDK (the project was build using JDK23, however, different
versions have not been tested and may work as well) installed and added to your `PATH`.

Run the script using the following command from the project root:
  - `python3 scripts/build/build_jar_files.py --modules 'specific modules separated by a single space'`
  - `python3 scripts/build/build_jar_files.py --modules auth appointments diagnoses`

The script has an optional `--modules` parameter. When it is not passed, the script will build all submodules one after
the other. To specify the submodules you want to build, you need to pass them like this `--modules users api auth`.
Modules need to be separated by a single space for the script to be able to recognize them.

The available modules are:
  - `api`
  - `appointments`
  - `auth`
  - `diagnoses`
  - `users`

Passing incorrect modules will result in 0 build modules!

Passing a combination of correct and incorrect modules will result in building only the correct ones!

The module names are directly correlated to the module directory, so renaming the submodule directories will render this
script unusable!

## Script - `build_docker_images.py`
This script is very similar to the `build_jar_files.py` script in terms of use. To run the script you need to enter the
following command from project root:
  - `python3 scripts/build/build_docker_images.py --modules 'specific modules separated by a single space'`
  - `python3 scripts/build/build_docker_images.py --modules api frontend users`

Just like the `build_jar_files.py` script, this one has an optional `--modules` parameter. When not specified, the
script will build all submodule Dockerfiles.

The available modules are:
  - `api`
  - `appointments`
  - `auth`
  - `diagnoses`
  - `users`
  - `frontend`