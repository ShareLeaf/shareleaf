import { FC } from 'react';
import PropTypes from 'prop-types';

import { styled, Typography, Box, Divider } from '@mui/material';

interface PageHeaderProps {
  heading: string;
  subheading: string;
}

const RootWrapper = styled(Box)(
  ({ theme }) => `
        margin-top: ${theme.spacing(5)};
`
);

const PageHeader: FC<PageHeaderProps> = ({ heading, subheading, ...rest }) => {

  return (
    <RootWrapper {...rest}>
      {heading && <Typography variant="h1">{(heading)}</Typography>}
      {subheading && (
        <Typography variant="subtitle2">{(subheading)}</Typography>
      )}
      <Divider
        sx={{
          mt: 5
        }}
      />
    </RootWrapper>
  );
};

PageHeader.propTypes = {
  heading: PropTypes.string,
  subheading: PropTypes.string
};

export default PageHeader;
