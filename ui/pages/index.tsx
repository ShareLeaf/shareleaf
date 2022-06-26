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

interface OverViewProps {
    title: string;
    description: string;
    siteImage: string;
}

// @ts-ignore
export async function getServerSideProps({ req, res }) {
    // Allow caching of the home page for 259200 seconds (3 days)
    // and allow using the stale page for up to 86400 (1 day) until
    // the cache is refreshed
    res.setHeader(
        'Cache-Control',
        'public, s-maxage=259200, stale-while-revalidate=86400'
    )

    return {
        props: {
            title: "ShareLeaf",
            description: "Share Content from Anywhere",
            siteImage: "https://shareleaf.co/static/images/placeholders/covers/shareleaf.png"
        }
    }
}

function Overview(props: OverViewProps) {
    return (
        <>
            <Head>
                <title>{props.title}</title>
                <meta property="og:title" content={props.title} key="title"/>
                <meta property="og:description" content={props.description} key="description"/>
                <meta property="og:image" content={props.siteImage} key="image"/>
            </Head>
        <OverviewWrapper>
            <Common
                title={props.title}
                children={<LinkGenerator />}/>
        </OverviewWrapper>
        </>
    );
}

export default Overview;

Overview.getLayout = function getLayout(page: ReactElement) {
    return <BaseLayout>{page}</BaseLayout>;
};
