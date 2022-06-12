import {
    Alert,
    Box,
    Container,
    Grid,
    Typography,
    Card,
    Divider,
    Button,
    CardActions,
    styled, Tooltip
} from '@mui/material';
import React, {FC, useEffect, useState} from "react";
import axios from "axios";
import VideoElement from "src/content/Media/VideoElement";
import Loader from 'src/components/Loader';
import Common from "@/content/Common";
import ImageElement from "src/content/Media/ImageElement";
import Head from "next/head";
import ShareTwoToneIcon from '@mui/icons-material/ShareTwoTone';
import {copyToClipBoard} from "@/content/utils/utils";

const OverviewWrapper = styled(Box)(
    ({ theme }) => `
    background: ${theme.palette.common.white};
    flex: 1;
`
);

const CardActionsWrapper = styled(CardActions)(
    ({ theme }) => `
     background: ${theme.colors.alpha.black[5]};
     padding-left: ${theme.spacing(3)};
     padding-right: ${theme.spacing(3)};
     padding-top: ${theme.spacing(1)};
     padding-bottom: ${theme.spacing(3)};
     // float: right;
`
);
const ButtonWrapper = styled(Button)(
    ({ theme }) => `
    color: ${theme.colors.primary.main};
    background: ${theme.colors.alpha.white[70]};
    font-weight: normal;
    
    &:hover {
      color: ${theme.colors.alpha.black[100]};
      background: ${theme.colors.alpha.white[100]};
    }
`
);

const DividerWrapper = styled(Divider)(
    ({ theme }) => `
    background: ${theme.colors.alpha.black[10]};
`
);

const BoxWrapper = styled(Box)(
    ({ theme }) => `
    border-radius: ${theme.general.borderRadius};
    background: ${theme.colors.alpha.black[10]};
`
);
interface MediaMetadataProps {
    id?: string,
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
    thumbnail?: string,
    shareableLink?: string,
    viewCount?: number,
    shareCount?: number
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
                id: context.query.id,
                encoding: response.data.encoding,
                title: response.data.title,
                description: response.data.description,
                type: response.data.media_type,
                url: response.data.url,
                viewCount: response.data.view_count,
                shareCount: response.data.share_count,
                shareableLink: response.data.shareable_link,
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
    const [open, setOpen] = useState<boolean>(false);

    const handleOpen = () => {
        if (metadata.type === "image") {
            setOpen(!open);
        }
    }

    const handleShare = async () : Promise<void> => {
        const data = { uid: props.id};
        axios.post(`${process.env.REACT_APP_SERVER_BASE_URL}/update-share`, {...data})
            .then(() => {})
            .catch(e => console.log(e));
        await copyToClipBoard(props.shareableLink)
    }

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
                // console.log("prps: ", props)
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
                        <Container maxWidth="md" sx={{ padding: 0, mb: 10, mt: 3}}>
                            <Grid
                                justifyContent="center"
                                alignItems="center"
                                container
                                sx={{
                                    textAlign: "center",
                                    p: 0
                                }}
                            >
                                <Grid item md={6} xs={12}>
                                <Card>
                                    <CardActionsWrapper
                                        sx={{
                                            display: { xs: 'block', md: 'flex' },
                                            alignItems: 'right',
                                            justifyContent: 'right',
                                            float: 'right',
                                            width: '100%'
                                        }}
                                    >
                                        <Box sx={{padding: 0, float: 'right'}}>
                                            <Tooltip title={'Copy URL to Share'} arrow>
                                            <Button startIcon={<ShareTwoToneIcon />}
                                                    onClick={handleShare}
                                                    variant="outlined">
                                                {('Share')}
                                            </Button>
                                            </Tooltip>
                                        </Box>
                                    </CardActionsWrapper>
                                    {(metadata.type === "video" || metadata.type === "gif") &&
                                        <VideoElement
                                            thumbnail={metadata.thumbnail}
                                            encoding={metadata.encoding}
                                            src={metadata.url}
                                            title={metadata.title}
                                        />
                                    }
                                    {metadata.type === "image" &&
                                            <ImageElement
                                                handleOpen={handleOpen}
                                                src={metadata.url}
                                                title={metadata.title}
                                            />
                                    }
                                    <Divider />
                                    <CardActionsWrapper
                                        sx={{
                                            width: '100%',
                                            display: { xs: 'flex', md: 'flex' },
                                            alignItems: 'center',
                                            justifyContent: 'space-between'
                                        }}
                                    >
                                        <BoxWrapper
                                            sx={{width: '100%'}}
                                            display="flex"
                                            alignItems="stretch"
                                            justifyContent="space-between"
                                            mt={2}
                                            p={1}
                                        >
                                            <Box display="flex" alignItems="center">
                                                <ButtonWrapper
                                                    size="small"
                                                    color="secondary"
                                                    variant="text"
                                                >
                                                    <b>{metadata.shareCount}</b>&nbsp;{('shares')}
                                                </ButtonWrapper>
                                                <DividerWrapper
                                                    sx={{
                                                        mx: 2
                                                    }}
                                                    orientation="vertical"
                                                    flexItem
                                                />
                                                <Typography variant="body2" color="text.primary">
                                                    <b>{metadata.viewCount}</b> {('views')}
                                                </Typography>
                                            </Box>
                                        </BoxWrapper>
                                        <Box sx={{width: "25px", mt: 2, ml: 3}}
                                             component="img"
                                             alt="ShareLeaf"
                                             src="/favicon-32x32.png"
                                        />

                                    </CardActionsWrapper>
                                </Card>
                                </Grid>
                            </Grid>
                        </Container>
                    }/>
            </OverviewWrapper>
        );
        if (open) {
            return (
                <dialog
                    className="image-dialog"
                    style={{ position: "absolute", margin: 0, padding: 0, width: '100%' }}
                    open
                    onClick={handleOpen}
                >
                    <img
                        style={{margin: 0, padding: 0}}
                        src={metadata.url}
                        onClick={handleOpen}
                        alt={metadata.title}
                    />
                </dialog>
            )
        }
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
                Sorry, we were not able to process that URL. It may not be supported at the moment. Please DM us the link here
                https://twitter.com/share_leaf and we'll investigate.
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