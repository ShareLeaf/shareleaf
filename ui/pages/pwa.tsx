import {
    Box,
    Container,
    Grid,
    Typography,
    Button,
    styled,
} from '@mui/material';
import React, {FC, useEffect, useState} from "react";
import Common from "@/content/Common";
import Head from "next/head";

const OverviewWrapper = styled(Box)(
    ({ theme }) => `
    background: ${theme.palette.common.white};
    flex: 1;
`
);




const TypographyH1 = styled(Typography)(
    ({ theme }) => `
    font-size: ${theme.typography.pxToRem(50)};
`
);

const TypographyH2 = styled(Typography)(
    ({ theme }) => `
    font-size: ${theme.typography.pxToRem(17)};
`
);


const LabelWrapper = styled(Box)(
    ({ theme }) => `
    background-color: ${theme.colors.success.main};
    color: ${theme.palette.success.contrastText};
    font-weight: bold;
    border-radius: 30px;
    text-transform: uppercase;
    display: inline-block;
    font-size: ${theme.typography.pxToRem(11)};
    padding: ${theme.spacing(0.5)} ${theme.spacing(1.5)};
    margin-bottom: ${theme.spacing(2)};
`
);


export async function getServerSideProps(_context) {
    let props = {}
    try {
        props = {
            title: "Use ShareLeaf as an App",
            description: "ShareLeaf is a PWA app and can be used on mobile devices like a regular app"
        }
    } catch (e) {
        console.log(e)
    }
    return {
        props: props
    };
}

const PWA: FC<{title, description}> = (props) => {
    // @ts-ignore
    const [supportsPWA, setSupportsPWA] = useState(false);
    const [promptInstall, setPromptInstall] = useState(null);

    useEffect(() => {
        const handler = e => {
            e.preventDefault();
            setSupportsPWA(true);
            setPromptInstall(e);
        };
        window.addEventListener("beforeinstallprompt", handler);

        return () => window.removeEventListener("transitionend", handler);
    });

    const handleInstallClick = evt => {
        evt.preventDefault();
        if (!promptInstall) {
            return;
        }
        promptInstall.prompt();
    };
  const component =  <OverviewWrapper>
      <Common
          showHighlights={false}
          title={props.title}
          children={
              <Container maxWidth="lg" sx={{ padding: 10, mb: 10, mt: 3}}>
                  <Grid
                      spacing={{ xs: 6, md: 2 }}
                      justifyContent="center"
                      alignItems="center"
                      container
                  >
                      <Grid item md={8} pr={{ xs: 0, md: 3 }} sm={12}>
                          <Box>
                          <LabelWrapper color="success">{('Version') + ' 1.0'}</LabelWrapper>
                          <TypographyH1
                              sx={{
                                  mb: 2
                              }}
                              variant="h1"
                          >
                              {('ShareLeaf is a Progressive Web App (PWA)')}
                          </TypographyH1>
                          <TypographyH2
                              sx={{
                                  lineHeight: 1.5,
                                  pb: 4
                              }}
                              variant="h4"
                              color="text.secondary"
                              fontWeight="normal"
                          >
                              {(
                                  'ShareLeaf is better when used as a PWA than as a website on mobile. As a PWA, ShareLeaf function the same way as a regular mobile app. It is extremely light and can be added or removed from your device with one click. ' +
                                  'With a PWA, you will receive updates instantly without having to go to the App/Play Store.'
                              )}
                          </TypographyH2>
                          </Box>
                          <Box>
                          <Typography
                              textAlign="left"
                              sx={{
                                  mb: 2,
                                  paddingBottom: '1rem'
                              }}
                              color="primary"
                              variant="h3"
                          >
                              Android
                          </Typography>

                          <Button
                              title="Share Reels from Instagram with ShareLeaf"
                              aria-label="Install ShareLeaf"
                              size="large"
                              color="info"
                              variant="contained"
                              onClick={handleInstallClick}
                          >
                              {('Install ShareLeaf')}
                          </Button>
                              <Typography
                                  sx={{
                                      pt: 1
                                  }}
                                  variant="subtitle2"
                              >
                                  Wait for the popup and click to install.
                              </Typography>
                              <Typography
                                  sx={{
                                      pt: 1
                                  }}
                                  variant="subtitle2"
                              >
                                  Read more about PWAs on Android at
                              </Typography>
                              <a href="https://support.google.com/chrome/answer/9658361?co=GENIE.Platform%3DAndroid"
                              style={{textDecoration: "none"}}>
                              <Typography
                                  sx={{
                                      pt: 0,
                                      textDecoration: "none"
                                  }}
                                  variant="subtitle2"
                              >
                                  https://support.google.com/chrome/answer/9658361?co=GENIE.Platform%3DAndroid
                              </Typography>
                              </a>
                          </Box>

                          <Box sx={{marginTop: '4rem'}}>
                              <Typography
                                  textAlign="left"
                                  sx={{
                                      mb: 1,
                                      paddingBottom: '0.5rem',
                                  }}
                                  color="primary"
                                  variant="h3"
                              >
                                  iOS
                              </Typography>


                              <Typography
                                  sx={{
                                      pt: 1
                                  }}
                                  variant="subtitle2"
                              >
                                 1. Tap the share button on Safari's menu bar
                              </Typography>
                              <Typography
                                  sx={{
                                      pt: 1
                                  }}
                                  variant="subtitle2"
                              >
                                  2. Tap 'Add to Home Screen'
                              </Typography>

                              <Typography
                                  sx={{
                                      pt: 1
                                  }}
                                  variant="subtitle2"
                              >
                                  Read more about PWAs on iOS at
                              </Typography>
                              <a href="https://mobilesyrup.com/2020/05/24/how-install-progressive-web-app-pwa-android-ios-pc-mac/"
                                 style={{textDecoration: "none"}}>
                                  <Typography
                                      sx={{
                                          pt: 0,
                                          textDecoration: "none"
                                      }}
                                      variant="subtitle2"
                                  >
                                      https://mobilesyrup.com/2020/05/24/how-install-progressive-web-app-pwa-android-ios-pc-mac/
                                  </Typography>
                              </a>
                          </Box>

                      </Grid>
                      <Grid item md={4} sm={8}>
                          <Box>
                              <img
                                  style={{maxWidth: "380px", borderRadius: '16px'}}
                                  alt="ShareLeaf PWA"
                                  src="/static/images/app/shareleaf-app.jpeg"
                              />
                          </Box>
                      </Grid>
                  </Grid>
              </Container>
              
          }/>
  </OverviewWrapper>
    return typeof window === 'undefined' ? null :
     <>
        <Head>
            <title>{props.title}</title>
            <meta property="og:title" content={props.title} key="title"/>
            <meta property="og:description" content={props.description} key="description"/>
        </Head>
        {component}
    </>

}

export default PWA;