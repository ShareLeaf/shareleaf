import {Box, Card, Typography, styled, IconButton} from '@mui/material';
import Link from 'src/components/Link';
import TwitterIcon from '@mui/icons-material/Twitter';

const FooterWrapper = styled(Card)(
  ({ theme }) => `
        border-radius: 0;
        margin-top: ${theme.spacing(4)};
`
);

function Footer() {
  return (
    <FooterWrapper className="footer-wrapper">
      <Box
        p={4}
        display={{ xs: 'block', md: 'flex' }}
        alignItems="center"
        textAlign={{ xs: 'center', md: 'left' }}
        justifyContent="space-between"
      >
        <Box>
          <Typography variant="subtitle1">
            &copy; 2022 - ShareLeaf
          </Typography>
        </Box>
        <Typography
          sx={{
            pt: { xs: 2, md: 0 }
          }}
          variant="subtitle1"
        >
          <Link
            href="https://twitter.com/share_leaf"
            target="_blank"
            rel="noopener noreferrer"
          >
            <IconButton sx={{ mx: 1 }} color="info" component="span">
              <TwitterIcon fontSize="small" />
            </IconButton>
          </Link>
        </Typography>
      </Box>
    </FooterWrapper>
  );
}

export default Footer;
