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
import AddLinkIcon from '@mui/icons-material/AddLink';
import CloudDownloadIcon from '@mui/icons-material/CloudDownload';
import TimerIcon from '@mui/icons-material/Timer';

const AvatarError = styled(Avatar)(
  ({ theme }) => `
    background: ${theme.colors.error.main};
    width: ${theme.spacing(4)};
    height: ${theme.spacing(4)};
`
);

const AvatarPrimary = styled(Avatar)(
    ({ theme }) => `
    background: ${theme.colors.primary.main};
    width: ${theme.spacing(4)};
    height: ${theme.spacing(4)};
`
);

const AvatarWarning = styled(Avatar)(
    ({ theme }) => `
    background: ${theme.colors.warning.main};
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
        maxWidth="md"
      >
        <TypographyH1Primary
          textAlign="center"
          sx={{
            mb: 4, paddingBottom: '4rem'
          }}
          variant="h5"
        >
         How does ShareLeaf work?
        </TypographyH1Primary>
        <Grid container spacing={2}>
            <Grid item xs={12} md={3} sx={{mb: {xs: 4, md: 2}}}>
                <Card
                    sx={{
                        overflow: 'visible',
                        height: '16rem'
                    }}
                >
                    <AvatarPrimary
                        sx={{
                            width: 60,
                            height: 60,
                            mx: 'auto',
                            position: 'relative',
                            top: -28
                        }}
                    >
                        <AddLinkIcon fontSize="large" />
                    </AvatarPrimary>
                    <Box
                        px={1}
                        pb={4}
                        display={{ xs: 'block', md: 'flex' }}
                        alignItems="flex-start"
                    >
                        <Box
                            sx={{
                                p: { xs: 1, md: 2 }
                            }}
                        >
                            <Typography variant="h4" textAlign="center">
                               Generate a Link
                            </Typography>
                            <Typography
                                sx={{
                                    pt: 1
                                }}
                                variant="subtitle2"
                            >
                                Paste a shareable link from a top social media site above and
                                generate a ShareLeaf link
                            </Typography>
                        </Box>
                    </Box>
                </Card>
            </Grid>
            <Grid item xs={12} md={3} sx={{mb: {xs: 4, md: 2}}}>
                <Card
                    sx={{
                        height: '16rem',
                        padding: 0,
                        paddingLeft: '0px',
                        overflow: 'visible'
                    }}
                >
                    <AvatarWarning
                        sx={{
                            width: 60,
                            height: 60,
                            mx: 'auto',
                            position: 'relative',
                            top: -28
                        }}
                    >
                        <CloudDownloadIcon fontSize="large" />
                    </AvatarWarning>
                    <Box
                        px={1}
                        pb={4}
                        display={{ xs: 'block', md: 'flex' }}
                        alignItems="flex-start"
                    >
                        <Box
                            sx={{
                                p: { xs: 1, md: 2 }
                            }}
                        >
                            <Typography variant="h4" textAlign="center">
                                Content Download
                            </Typography>
                            <Typography
                                sx={{
                                    pt: 1
                                }}
                                variant="subtitle2"
                            >
                                We will download the content immediately and make it available for viewing
                            </Typography>
                        </Box>
                    </Box>
                </Card>
            </Grid>
            <Grid item xs={12} md={3} sx={{mb: {xs: 4, md: 2}}}>
                <Card
                    sx={{
                        height: '16rem',
                        padding: 0,
                        paddingLeft: '0px',
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
                                p: { xs: 1, md: 2 }
                            }}
                        >
                            <Typography variant="h4" textAlign="center">
                                Share Anonymously
                            </Typography>
                            <Typography
                                sx={{
                                    pt: 1
                                }}
                                variant="subtitle2"
                            >
                               Share your ShareLeaf link with anyone and they can view the content without an account
                            </Typography>
                        </Box>
                    </Box>
                </Card>
            </Grid>
            <Grid item xs={12} md={3} sx={{mb: {xs: 4, md: 2}}}>
                <Card
                    sx={{
                        height: '16rem',
                        padding: 0,
                        paddingLeft: '0px',
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
                        <TimerIcon fontSize="large" />
                    </AvatarSuccess>
                    <Box
                        px={1}
                        pb={4}
                        display={{ xs: 'block', md: 'flex' }}
                        alignItems="flex-start"
                    >
                        <Box
                            sx={{
                                p: { xs: 1, md: 2 }
                            }}
                        >
                            <Typography variant="h4" textAlign="center">
                                ShareLeaf is Forever
                            </Typography>
                            <Typography
                                sx={{
                                    pt: 1
                                }}
                                variant="subtitle2"
                            >
                               Your shared content will live on ShareLeaf even if it disappears from the original source
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
