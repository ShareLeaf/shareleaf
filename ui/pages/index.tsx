import { Box, styled } from '@mui/material';
import type { ReactElement } from 'react';
import BaseLayout from 'src/layouts/BaseLayout';
import React from 'react';
import LinkGenerator from "@/content/LinkGenerator";
import Common from "@/content/Common";
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

export async function getServerSideProps() {
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
