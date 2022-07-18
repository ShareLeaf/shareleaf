import { Box, styled } from '@mui/material';
import type { ReactElement } from 'react';
import BaseLayout from 'src/layouts/BaseLayout';
import React from 'react';
import LinkGenerator from "@/content/LinkGenerator";
import Common from "src/content/Common";
import Head from "next/head";

const OverviewWrapper = styled(Box)(
    ({ theme }) => `
    background: ${theme.palette.common.white};
    flex: 1;
`
);

const metadata = {
    title: "ShareLeaf",
    description: "Share Content from Anywhere",
    siteImage: "https://shareleaf.co/static/images/placeholders/covers/shareleaf.png"
}

function Overview() {
    return (
        <>
            <Head>
                <title>{metadata.title}</title>
                <meta property="og:title" content={metadata.title} key="title"/>
                <meta property="og:description" content={metadata.description} key="description"/>
                <meta property="og:image" content={metadata.siteImage} key="image"/>
            </Head>
        <OverviewWrapper>
            <Common
                title={metadata.title}
                children={<LinkGenerator />}/>
        </OverviewWrapper>
        </>
    );
}

export default Overview;

Overview.getLayout = function getLayout(page: ReactElement) {
    return <BaseLayout>{page}</BaseLayout>;
};
