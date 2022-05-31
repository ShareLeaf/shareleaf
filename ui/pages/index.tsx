import { Box, styled } from '@mui/material';
import type { ReactElement } from 'react';
import BaseLayout from 'src/layouts/BaseLayout';
import React from 'react';
import LinkGenerator from "@/content/LinkGenerator";
import Common from "@/content/Common";

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
            <Common
                title={"ShareLeaf | Share Anything, with Anyone"}
                children={<LinkGenerator />}/>
        </OverviewWrapper>
    );
}

export default Overview;

Overview.getLayout = function getLayout(page: ReactElement) {
    return <BaseLayout>{page}</BaseLayout>;
};
