import {
    Box,
    Grid,
    Container,
    Card,
    Avatar,
    Typography,
    styled
} from '@mui/material';
import PersonOffIcon from '@mui/icons-material/PersonOff';
import SpeedIcon from '@mui/icons-material/Speed';

const AvatarError = styled(Avatar)(
  ({ theme }) => `
    background: ${theme.colors.error.main};
    width: ${theme.spacing(4)};
    height: ${theme.spacing(4)};
`
);

const AvatarSuccess = styled(Avatar)(
    ({ theme }) => `
    background: ${theme.colors.success.main};
    width: ${theme.spacing(4)};
    height: ${theme.spacing(4)};
`
);



const TypographyH1Primary = styled(Typography)(
  ({ theme }) => `
    font-size: ${theme.typography.pxToRem(28)};
`
);

const TypographyH2 = styled(Typography)(
  ({ theme }) => `
    font-size: ${theme.typography.pxToRem(17)};
`
);

const BoxHighlights = styled(Box)(
  () => `
    position: relative;
    z-index: 5;
`
);

function Highlights() {
  return (
    <BoxHighlights>
      <Container
        sx={{
          pt: { xs: 6, md: 12 },
          pb: { xs: 5, md: 15 }
        }}
        maxWidth="md"
      >
        <TypographyH1Primary
          textAlign="center"
          sx={{
            mb: 2
          }}
          variant="h1"
        >
          {('What is ShareLeaf?')}
        </TypographyH1Primary>
        <Container
          sx={{
            mb: 6,
            textAlign: 'center'
          }}
          maxWidth="sm"
        >
          <TypographyH2
            sx={{
              pb: 4,
              lineHeight: 1.5
            }}
            textAlign="center"
            variant="h4"
            color="text.secondary"
            fontWeight="normal"
          >
            {(
              'ShareLeaf allows you to share videos and images from Reddit, Facebook, Instagram, and Twitter with your friends by ' +
                'creating a unique ShareLeaf URL'
            )}
          </TypographyH2>
        </Container>
        <Grid container spacing={4}>
          <Grid item xs={12} md={6}>
            <Card
              sx={{
                overflow: 'visible'
              }}
            >
              <AvatarError
                sx={{
                  width: 60,
                  height: 60,
                  mx: 'auto',
                  position: 'relative',
                  top: -28
                }}
              >
                  <PersonOffIcon fontSize="large" />
              </AvatarError>
              <Box
                px={1}
                pb={4}
                display={{ xs: 'block', md: 'flex' }}
                alignItems="flex-start"
              >
                <Box
                  sx={{
                    pl: { xs: 0, md: 4 }
                  }}
                >
                  <Typography variant="h3">
                    No Account Needed
                  </Typography>
                  <Typography
                    sx={{
                      pt: 1
                    }}
                    variant="subtitle2"
                  >
                      If you have a ShareLeaf link, you can view content from any social media site without creating an account.
                      That means no tracking and more privacy to you.
                  </Typography>
                </Box>
              </Box>
            </Card>
          </Grid>
          <Grid item xs={12} md={6}>
            <Card
              sx={{
                mt: { xs: 5, md: 0 },
                overflow: 'visible'
              }}
            >
              <AvatarSuccess
                sx={{
                  width: 60,
                  height: 60,
                  mx: 'auto',
                  position: 'relative',
                  top: -28
                }}
              >
                  <SpeedIcon fontSize="large" />
              </AvatarSuccess>
              <Box
                px={1}
                pb={4}
                display={{ xs: 'block', md: 'flex' }}
                alignItems="flex-start"
              >
                <Box
                  sx={{
                    pl: { xs: 0, md: 4 }
                  }}
                >
                  <Typography variant="h3">
                    ShareLeaf is Fast
                  </Typography>
                  <Typography
                    sx={{
                      pt: 1
                    }}
                    variant="subtitle2"
                  >
                      Your don't need a fast Internet to view content on ShareLeaf. We distribute
                      everything to your nearest location.
                  </Typography>
                </Box>
              </Box>
            </Card>
          </Grid>
        </Grid>
      </Container>
    </BoxHighlights>
  );
}

export default Highlights;
