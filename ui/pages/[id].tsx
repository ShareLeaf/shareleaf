import {
    Alert,
    Box,
    Container,
    Grid, styled
} from '@mui/material';
import React, {FC, useEffect, useState} from "react";
import axios from "axios";
import VideoElement from "@/content/Media/VideoElement";
import Loader from 'src/components/Loader';
import Common from "@/content/Common";
import ImageElement from "@/content/Media/ImageElement";

const OverviewWrapper = styled(Box)(
    ({ theme }) => `
    overflow: auto;
    background: ${theme.palette.common.white};
    flex: 1;
    overflow-x: hidden;
`
);

interface MediaMetadata {
    encoding?: string,
    description?: string,
    title?: string,
    type?: string
    url?: string,
    error?: string
}

const Media: FC<any> = () => {
    const [metadata, setMetadata] = useState<MediaMetadata | undefined>(undefined);
    const [showError, setShowError] = useState<boolean>(false);
    const [showInProgress, setShowInProgress] = useState<boolean>(false);
    const pathTokens = window.location.pathname.split("/");
    useEffect(() => {
        if (pathTokens.length > 1) {
            const id = pathTokens[pathTokens.length-1]
            if (id) {
                setShowInProgress(false);
                setShowError(false);
                axios.get(`${process.env.REACT_APP_SERVER_BASE_URL}/metadata?key=` + id)
                    .then(response => {
                        if (response.data.error || response.data.invalid_url) {
                            setShowError(true);
                        } else {
                            if (response.data.processed) {
                                const parsed = {
                                    encoding: response.data.encoding,
                                    title: response.data.title,
                                    description: response.data.description,
                                    type: response.data.media_type,
                                    url: response.data.url
                                };
                                setMetadata(parsed);
                            } else {
                                setShowInProgress(true);
                            }

                        }
                    });
            }
        }

    }, [])

    if (metadata) {
        return (
            <OverviewWrapper>
                <Common
                    title={metadata.title}
                    children={
                        <Container maxWidth="lg">
                            <Grid
                                justifyContent="center"
                                alignItems="center"
                                container
                            >
                                <Grid item md={12}>
                                    {metadata.type === "video" &&
                                        <VideoElement
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
            return <Alert severity="info">
                We are currently processing this url. Please check back in a few minutes.
            </Alert>
        }
        else if (showError) {
            return <Alert severity="error">
                Sorry, could not find the page you're looking for :(
            </Alert>
        } else {
            return <Loader/>
        }
    }

}

export default Media;