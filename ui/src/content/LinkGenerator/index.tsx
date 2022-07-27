import React, {ChangeEvent, FC, useEffect, useState} from "react";
import {Box, Button, Container, Grid, IconButton, InputBase, styled, Typography} from "@mui/material";
import { RWebShare } from "react-web-share";
import IosShareIcon from '@mui/icons-material/IosShare';
import Typed from "react-typed";
import { ContentApi } from "src/api";
import { headerConfig } from "src/api/headerConfig";

const SearchInputWrapper = styled(InputBase)(
    ({ theme }) => `
    font-size: ${theme.typography.pxToRem(18)};
    padding-left: 1rem;
    padding-right: 1rem;
    padding-top: 5px;
    padding-bottom: 5px;
    width: 100%;
`
);

const SearchBoxWrapper = styled(Box)(
    () => `
     border: 1px solid #ededed;
     border-radius: 1rem;
     margin-top: 4rem;
     width: 100%;
`
);

const ShareableLinkWrapper = styled(Box)(
    ({ theme }) => `
   width: 100%;
   box-shadow: ${theme.colors.shadows.success};
`
);

const TypographyH1Primary = styled(Typography)(
    ({ theme }) => `
    font-size: ${theme.typography.pxToRem(28)};
`
);

const TypographyH1Socials = styled(Typography)(
    ({ theme }) => `
    background: linear-gradient(88.28deg,#0094FF 16.83%,#9B8FFE 44.77%,#FF33DE 71.88%,#FF60E6 98.01%);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    font-size: ${theme.typography.pxToRem(28)};
`
);

const LinkGenerator: FC<any> = () => {
    const [searchValue, setSearchValue] = useState<string>('');
    const [generatedUrl, setGeneratedUrl] = useState<string>('')

    const submitSearch = async (event): Promise<void> => {
        event.preventDefault();
        await generateContentId(searchValue);
    };

    const generateContentId = async (rawUrl: string): Promise<void> => {
        if (rawUrl && isValidUrl(rawUrl)) {
            new ContentApi(headerConfig()).generateContentId({url: rawUrl}).then(response => {
                setGeneratedUrl(response.data.shareable_link);
            }).catch(e => console.log(e))
        }
    }

    const isValidUrl = (rawUrl: string): boolean => {
        let validUrl;

        try {
            validUrl = new URL(rawUrl);
        } catch (_) {
            return false;
        }
        return validUrl.protocol === "http:" || validUrl.protocol === "https:";
    }

    const handleSearchChange = async (event: ChangeEvent<{ value: unknown }>) => {
        event.preventDefault();

        if (event.target.value) {
            setSearchValue(event.target.value as string);

        } else {
            setSearchValue('');
        }
    };

    useEffect(() => {


    })

    useEffect(() => {
        // Copy text from the clipboard at a set interval and attempt to generate a shareable link
        const localStorageKey = "shared-leaves";
        const clipboardInterval = setInterval(() => {
            if (navigator.clipboard) {
                navigator.clipboard.readText().then(text => {
                    if (isValidUrl(text)) {
                        const previousSharesStr: string = window.localStorage.getItem(localStorageKey);
                        let previousShares: string[] = [];
                        if (previousSharesStr) {
                            previousShares = JSON.parse(previousSharesStr);
                        }
                        // Have we previously generated a link for this url?
                        if (!previousShares.includes(text)) {
                            previousShares.push(text);
                            window.localStorage.setItem(localStorageKey, JSON.stringify(previousShares));
                            setSearchValue(text);
                            generateContentId(text)
                                .then()
                                .catch()
                        }
                    }
                }).catch();
            }
        }, 1000);
        return () => {
            clearInterval(clipboardInterval);
        };
    }, []);

    return (
        <Container
            sx={{
                pt: { xs: 6, md: 12 },
                pb: { xs: 5, md: 15 }
            }}>
            <Grid
                spacing={{ xs: 6, md: 10 }}
                justifyContent="center"
                alignItems="center"
                container
            >
                <Grid item xs={12} md={8}>
                    <Grid item xs={12}>
                        <Box>
                            <TypographyH1Primary
                                textAlign="center"
                                sx={{
                                    mb: 2
                                }}
                                variant="h5"
                            >
                                Instantly share videos, pictures, and reels from
                            </TypographyH1Primary>
                            <TypographyH1Socials
                                textAlign="center"
                                sx={{
                                    mb: 2
                                }}
                                variant="h2"
                            >
                                <Typed
                                    strings={["Reddit", "Instagram"]}
                                    typeSpeed={100}
                                    backSpeed={100}
                                    loop
                                />
                            </TypographyH1Socials>

                        </Box>
                        <SearchBoxWrapper>
                            <form onSubmit={submitSearch}>
                                <Box display="flex" alignItems="center">
                                    <Box flexGrow={1} display="flex" alignItems="center">
                                        <SearchInputWrapper
                                            value={searchValue}
                                            onChange={handleSearchChange}
                                            autoFocus
                                            placeholder={'Enter a link to share...'}
                                        />
                                    </Box>
                                    <Box
                                        sx={{

                                            ml: 'auto',
                                        }}
                                    >
                                        <Button
                                            sx={{
                                                padding: 0,
                                                paddingLeft: '24px',
                                                paddingRight: '24px',
                                                paddingTop: '12px',
                                                paddingBottom: '12px'
                                            }}
                                            size="medium"
                                            variant="contained"
                                            color="info"
                                            onClick={submitSearch}
                                        >
                                            Go
                                        </Button>
                                    </Box>
                                </Box>
                            </form>
                        </SearchBoxWrapper>
                    </Grid>
                    <Grid
                        justifyContent="center"
                        alignItems="center"
                        container
                        sx={{

                            ml: 'auto',
                            marginTop: '32px'
                        }}
                    >
                        <Button
                            sx={{
                                padding: 0,
                                paddingLeft: '24px',
                                paddingRight: '24px',
                                paddingTop: '12px',
                                paddingBottom: '12px'
                            }}
                            size="medium"
                            variant="contained"
                            color="success"
                            onClick={() => window.open("/pwa", "_blank")}
                        >
                            {'Use ShareLeaf as an App'}
                        </Button>
                    </Grid>
                    {generatedUrl &&  <Grid item xs={12}>
                        <Box sx={{mt: 3, ml: 2}}>
                            <Typography
                                variant="subtitle2"
                                sx={{pb: 1}}
                            >
                                Here's your shareable link
                            </Typography>
                            <ShareableLinkWrapper>
                                <Box display="flex" alignItems="center">
                                    <Box alignItems="center"
                                         sx={{
                                             ml: 2
                                         }}>
                                        <Typography
                                            variant="h4"
                                            color="text.secondary"
                                            fontWeight="bold"
                                        >
                                            {generatedUrl}
                                        </Typography>
                                    </Box>
                                    <Box
                                        display="flex"
                                        alignItems="left"
                                        sx={{
                                            m: 2,
                                            pl: 2
                                        }}
                                    >
                                        <IconButton sx={{ mx: 1 }}>
                                            <RWebShare
                                                data={{
                                                    text: "Check this out:" ,
                                                    url: generatedUrl,
                                                    title: "Share " + generatedUrl,
                                                }}
                                                sites={["whatsapp", "telegram", "facebook", "twitter", "reddit", "mail"]}
                                            >
                                                <IosShareIcon fontSize="medium" />
                                            </RWebShare>
                                        </IconButton>
                                    </Box>
                                </Box>
                            </ShareableLinkWrapper>
                        </Box>
                    </Grid>}
                </Grid>

            </Grid>
        </Container>
    );
}

export default LinkGenerator;