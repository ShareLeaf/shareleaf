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
    encoding?: string
    caption?: string,
    type?: string
    cdn?: string,
    error?: string
}

const Media: FC<any> = () => {
    const [metadata, setMetadata] = useState<MediaMetadata | undefined>(undefined);
    const [showError, setShowError] = useState<boolean>(false);
    const pathTokens = window.location.pathname.split("/");
    useEffect(() => {
        if (pathTokens.length > 1) {
            const id = pathTokens[pathTokens.length-1]
            if (id) {
                axios.get('http://127.0.0.1:5000/metadata?key=' + id)
                    .then(response => {
                        if (response.data.error) {
                            setShowError(true);
                        } else {
                            const parsed = {
                                encoding: response.data.encoding,
                                caption: response.data.caption,
                                type: response.data.type,
                                cdn: response.data.cdn
                            };
                            setMetadata(parsed);
                        }
                    });
            }
        }

    }, [])

    if (metadata) {
        const tempVidId = "/2010/05/sintel/trailer.mp4";
        const tempImageId = "https://picsum.photos/1000/600";
        return (
            <OverviewWrapper>
                <Common
                    title={metadata.caption}
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
                                            src={"https://d1x2gijf3bj73j.cloudfront.net/X8MQK3.mp4"}
                                            // src={metadata.cdn + tempVidId}
                                            caption={metadata.caption}
                                        />
                                    }
                                    {metadata.type === "image" &&
                                        <ImageElement
                                            src={tempImageId}
                                            caption={metadata.caption}
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
        if (showError) {
            return <Alert severity="error">
                Sorry, could not find the page you're looking for :(
            </Alert>
        } else {
            return <Loader/>
        }
    }

}

export default Media;