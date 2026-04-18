import logging
import os
import subprocess

def _setup_logging():
    logging.basicConfig(
        level=logging.INFO,
        format='%(asctime)s - %(levelname)s - %(message)s',
        datefmt='%Y-%m-%d %H:%M:%S'
    )

    return logging.getLogger(__name__)


def apply_namespace():
    logger = _setup_logging()

    root_directory = os.path.dirname(os.path.dirname(os.path.dirname(__file__)))

    cmd = ["kubectl", "apply", "-f", "k8s/namespace.yaml"]
    logger.info("Applying 'medical-record' namespace to Kubernetes cluster")

    try:
        subprocess.run(
            cmd,
            cwd=root_directory,
            check=True,
            stdout=subprocess.PIPE,
            stderr=subprocess.PIPE,
            text=True
        )

        logger.info("Namespace successfully applied")
    except subprocess.CalledProcessError as e:
        logger.error(f"An error occured: {e.stderr}")

if __name__ == "__main__":
    apply_namespace()