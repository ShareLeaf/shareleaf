import {
    Alert,
    Box,
    Container,
    Grid, styled
} from '@mui/material';
import React, {FC, useEffect, useState} from "react";
import axios from "axios";
import VideoElement from "src/content/Media/VideoElement";
import Loader from 'src/components/Loader';
import Common from "@/content/Common";
import ImageElement from "src/content/Media/ImageElement";
import Head from "next/head";

const OverviewWrapper = styled(Box)(
    ({ theme }) => `
    background: ${theme.palette.common.white};
    flex: 1;
`
);

interface MediaMetadataProps {
    encoding?: string,
    description?: string,
    title?: string,
    type?: string
    url?: string,
    error?: boolean,
    invalidUrl?: boolean,
    inProgress?: boolean,
    processed?: boolean,
    mediaType?: string,
    thumbnail?: string
}

export async function getServerSideProps(context) {
    const response = await axios.get(`${process.env.REACT_APP_SERVER_BASE_URL}/metadata?key=` + context.query.id)
    let props = {};
    if (response.data.error) {
        props = {error: true}
    } else if (response.data.invalid_url) {
        props= {invalidUrl: true}
    } else {
        if (response.data.processed) {
            props = {
                encoding: response.data.encoding,
                title: response.data.title,
                description: response.data.description,
                type: response.data.media_type,
                url: response.data.url,
                processed: true,
                thumbnail: response.data.thumbnail
            };
        } else {
            props = {inProgress: true}
        }
    }
    return {props: props}
}

const Media: FC<MediaMetadataProps> = (props) => {
    const [metadata, setMetadata] = useState<MediaMetadataProps | undefined>(undefined);
    const [showError, setShowError] = useState<boolean>(false);
    const [showInvalidUrlError, setShowInvalidUrlError] = useState<boolean>(false);
    const [showInProgress, setShowInProgress] = useState<boolean>(false);
    useEffect(() => {
        setShowInProgress(false);
        setShowError(false);
        setShowInvalidUrlError(false);
        if (props.error) {
            setShowError(true);
        } else if (props.invalidUrl) {
            setShowInvalidUrlError(true);
        } else {
            if (props.processed) {
                setMetadata(props);
            } else {
                setShowInProgress(true);
            }
        }

    }, [])

    let component = undefined;
    if (metadata) {
        component = (

            <OverviewWrapper>
                <Common
                    title={metadata.title}
                    children={
                        <Container maxWidth="md" sx={{ p: 0, mb: 10}}>
                            <Grid
                                justifyContent="center"
                                alignItems="center"
                                container
                                sx={{
                                    textAlign: "center",
                                    p: 0
                                }}
                            >
                                <Grid item md={8} xs={12}>
                                    {metadata.type === "video" &&
                                        <VideoElement
                                            thumbnail={metadata.thumbnail}
                                            encoding={metadata.encoding}
                                            src={metadata.url}
                                            title={metadata.title}
                                        />
                                    }
                                    {metadata.type === "image" &&
                                        <ImageElement
                                            src={metadata.url}
                                            title={metadata.title}
                                        />
                                    }
                                </Grid>
                            </Grid>
                        </Container>
                    }/>
            </OverviewWrapper>
        );
    }
    else {
        if (showInProgress) {
            component = <Alert severity="info">
                We are currently processing this url. Please check back in a few minutes.
            </Alert>
        }
        else if (showError) {
            component = <Alert severity="error">
                Sorry, could not find the page you're looking for :(
            </Alert>
        }  else if (showInvalidUrlError) {
            component = <Alert severity="warning">
                Sorry, we were not able to process the original URL associated with the ShareLeaf URL :(
            </Alert>
        } else {
            component = <Loader/>
        }
    }
    return <>
        <Head>
            <title>{props.title}</title>
            <meta property="og:title" content={props.title} key="title"/>
            <meta property="og:description" content={props.description} key="description"/>
            <meta property="og:image" content={props.thumbnail} key="image"/>
        </Head>
        {component}
    </>

}

export default Media;