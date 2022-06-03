from alembic.config import Config
from alembic import command
import sys
from os.path import dirname

sys.path.append(dirname(__file__).split("/server")[0])
from server.props.datasource import DatasourceProps


def migrate(app, datasource: DatasourceProps):
    if not datasource:
        print("Datasource configuration not found. Shutting down application...")
        exit(1)
    with app.app_context():
        ini_file = dirname(__file__).split("/server")[0] + "/server/alembic.ini"
        script_loc = dirname(__file__).split("/server")[0] + "/server/migrations"
        alembic_cfg = Config(ini_file)

        alembic_cfg.set_main_option("script_location", script_loc)
        alembic_cfg.set_main_option("sqlalchemy.url", "postgresql://{}:{}@{}".format(datasource.username,
                                                                                     datasource.password,
                                                                                     datasource.url))
        command.upgrade(alembic_cfg, 'head')
