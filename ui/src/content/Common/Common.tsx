import React, {FC, ReactNode} from "react";
import {Box, Container} from "@mui/material";
import Head from "next/head";
import FullLogo from "@/components/Logo/FullLogo";
import Highlights from "src/content/Overview/Highlights/Highlights";
import Footer from "@/components/Footer";

interface CommonProps {
    title: string,
    children: ReactNode;
}

const Common: FC<CommonProps> = (props) => {
    return (
        <>
            <Head>
                <title>{props.title}</title>
            </Head>
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
            <Highlights />
            <Footer />
        </>
    );
}
export default Common;