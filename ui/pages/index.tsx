import { Box, Container, styled } from '@mui/material';
import type { ReactElement } from 'react';
import BaseLayout from 'src/layouts/BaseLayout';

import Head from 'next/head';
import Hero from 'src/content/Overview/Hero';
import Highlights from 'src/content/Overview/Highlights';
import Footer from 'src/components/Footer';
import FullLogo from "@/components/Logo/FullLogo";
import Link from 'src/components/Link';

const OverviewWrapper = styled(Box)(
    ({ theme }) => `
    overflow: auto;
    background: ${theme.palette.common.white};
    flex: 1;
    overflow-x: hidden;
`
);

function Overview() {
    return (
        <OverviewWrapper>
            <Head>
                <title>ShareLeaf | Share Anything, with Anyone</title>
            </Head>
            {/*<HeaderWrapper>*/}
            <Container maxWidth="lg">
                <Box display="flex" alignItems="center" sx={{marginTop: '10px'}}>
                    {/*<Link href="/">*/}
                        <FullLogo />
                    {/*</Link>*/}

                    <Box
                        display="flex"
                        alignItems="center"
                        justifyContent="space-between"
                        flex={1}
                    >
                        <Box />
                    </Box>
                </Box>
            </Container>
            {/*</HeaderWrapper>*/}
            <Hero />
            <Highlights />
            <Footer />
        </OverviewWrapper>
    );
}

export default Overview;

Overview.getLayout = function getLayout(page: ReactElement) {
    return <BaseLayout>{page}</BaseLayout>;
};
