import {
  Container,
  Grid
} from '@mui/material';
import Media from "@/content/Media";

function Hero() {
  return (
    <Container maxWidth="lg">
      <Grid
        spacing={{ xs: 6, md: 10 }}
        justifyContent="center"
        alignItems="center"
        container
      >
        <Grid item md={12}>
          <Media src={"google.com"} type={"video"}/>
        </Grid>
      </Grid>
    </Container>
  );
}

export default Hero;
