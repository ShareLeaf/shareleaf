import React, {FC, ReactNode} from "react";
import {Box, Container} from "@mui/material";
import Head from "next/head";
import FullLogo from "@/components/Logo/FullLogo";
import Footer from "@/components/Footer";
import Highlights from "@/content/Highlights";

interface CommonProps {
    title: string,
    children: ReactNode;
    showHighlights: boolean
}

const Common: FC<CommonProps> = (props) => {
    return (
        <>
            <Head>
                <title>{props.title}</title>
            </Head>
            <Container sx={{height: '100%', padding: 0}}>
            <Container maxWidth="lg">
                <Box display="flex" alignItems="center" sx={{marginTop: '10px'}}>
                    <FullLogo />
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
            {props.children}
            {props.showHighlights && <Highlights />}
            </Container>
            <Footer />
        </>
    );
}
export default Common;