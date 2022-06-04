import type { ReactElement, ReactNode } from 'react';

import type { NextPage } from 'next';
import type { AppProps } from 'next/app';
import Head from 'next/head';
import Router from 'next/router';
import nProgress from 'nprogress';
import 'nprogress/nprogress.css';
import ThemeProvider from 'src/theme/ThemeProvider';
import CssBaseline from '@mui/material/CssBaseline';
import { CacheProvider, EmotionCache } from '@emotion/react';
import createEmotionCache from 'src/createEmotionCache';
import { appWithTranslation } from 'next-i18next';
import { SidebarProvider } from 'src/contexts/SidebarContext';
import AdapterDateFns from '@mui/lab/AdapterDateFns';
import LocalizationProvider from '@mui/lab/LocalizationProvider';
import { SnackbarProvider } from 'notistack';

const clientSideEmotionCache = createEmotionCache();

type NextPageWithLayout = NextPage & {
  getLayout?: (page: ReactElement) => ReactNode;
};

interface MyAppProps extends AppProps {
  emotionCache?: EmotionCache;
  Component: NextPageWithLayout;
}

function MyApp(props: MyAppProps) {
  const { Component, emotionCache = clientSideEmotionCache, pageProps } = props;
  const getLayout = Component.getLayout ?? ((page) => page);

  Router.events.on('routeChangeStart', nProgress.start);
  Router.events.on('routeChangeError', nProgress.done);
  Router.events.on('routeChangeComplete', nProgress.done);

  return (
    <CacheProvider value={emotionCache}>
      <Head>
        <title>ShareLeaf</title>
        <meta
          name="viewport"
          content="width=device-width, initial-scale=1, shrink-to-fit=no"
        />
      </Head>
        <SidebarProvider>
          <ThemeProvider>
            <LocalizationProvider dateAdapter={AdapterDateFns}>
                <SnackbarProvider
                  maxSnack={6}
                  anchorOrigin={{
                    vertical: 'bottom',
                    horizontal: 'right'
                  }}
                >
                  <CssBaseline />
                    {getLayout(<Component {...pageProps} />)}
                </SnackbarProvider>
            </LocalizationProvider>
          </ThemeProvider>
        </SidebarProvider>
    </CacheProvider>
  );
}

export default appWithTranslation(MyApp);
