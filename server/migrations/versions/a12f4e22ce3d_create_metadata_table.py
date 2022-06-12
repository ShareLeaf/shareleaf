"""create metadata table

Revision ID: a12f4e22ce3d
Revises: 
Create Date: 2022-06-02 20:25:00.435236

"""
from alembic import op
import sqlalchemy as sa


# revision identifiers, used by Alembic.
revision = 'a12f4e22ce3d'
down_revision = None
branch_labels = None
depends_on = None


def upgrade():
    op.create_table(
        'metadata',
        sa.Column('id', sa.String, primary_key=True, index=True),
        sa.Column('invalid_url', sa.Boolean()),
        sa.Column('processed', sa.Boolean()),
        sa.Column('encoding', sa.String()),
        sa.Column('media_type', sa.String()),
        sa.Column('title', sa.String()),
        sa.Column('description', sa.String()),
        sa.Column('category', sa.String()),
        sa.Column('canonical_url', sa.String(), index=True),
        sa.Column('view_count', sa.BigInteger()),
        sa.Column('share_count', sa.BigInteger()),
        sa.Column('like_count', sa.BigInteger()),
        sa.Column('dislike_count', sa.BigInteger()),
        sa.Column('created_dt', sa.DateTime(), default=sa.func.now()),
        sa.Column('updated_at', sa.DateTime(), default=sa.func.now()))


def downgrade():
    op.drop_table('metadata')
