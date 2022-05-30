import { Box, Container, Card } from '@mui/material';
import { Helmet } from 'react-helmet-async';

import { styled } from '@mui/material/styles';
import Hero from './Hero';
import {LogoContainer} from "../pages/Landing/components/Header/styles";
import {SvgIcon} from "../pages/Landing/common/SvgIcon";

const OverviewWrapper = styled(Box)(
  () => `
    overflow: auto;
    flex: 1;
    overflow-x: hidden;
    align-items: center;
`
);

function Overview() {

  return (
    <OverviewWrapper>
      <Helmet>
        <title>ResumeRepo CV Versioning and Tracking</title>
      </Helmet>
      <Container maxWidth="lg">
        <Box display="flex" justifyContent="center" py={5} alignItems="center">
            <LogoContainer to="/" aria-label="homepage">
                <SvgIcon src="logo.svg" width="186px" height="100%" />
            </LogoContainer>
        </Box>
        <Card sx={{ p: 10, mb: 10, borderRadius: 12 }}>
          <Hero />
        </Card>
      </Container>
    </OverviewWrapper>
  );
}

export default Overview;
