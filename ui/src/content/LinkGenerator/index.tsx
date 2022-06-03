import {ChangeEvent, FC, useState} from "react";
import {Box, Button, Container, Grid, IconButton, InputBase, styled, Typography} from "@mui/material";
import ContentCopyIcon from '@mui/icons-material/ContentCopy';
import axios from "axios";

const SearchInputWrapper = styled(InputBase)(
    ({ theme }) => `
    font-size: ${theme.typography.pxToRem(18)};
    padding: ${theme.spacing(2)};
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
    font-size: ${theme.typography.pxToRem(36)};
`
);

const LinkGenerator: FC<any> = () => {
    const [searchValue, setSearchValue] = useState<string>('');
    const [generatedUrl, setGeneratedUrl] = useState<string>('')

    const submitSearch = async (event): Promise<void> => {
        event.preventDefault();
        if (searchValue) {
            const data = { src: searchValue};
            axios.post('http://127.0.0.1:5000/generate-content-id', data)
                .then((result) => {
                    const uid = result.data.uid;
                    if (uid && uid.length > 0) {
                        setGeneratedUrl("http://localhost:3000/" + uid);
                        axios.post('http://127.0.0.1:5000/process-url', {...data, uid})
                            .then(() => {})
                            .catch(e => console.log(e));
                    }
                })
                .catch(e => console.log(e));
        }
    };

    const copyToClipBoard = async () : Promise<void> => {
        const ta = document.createElement("textarea");
        ta.innerText = generatedUrl;
        document.body.appendChild(ta);
        ta.select();
        document.execCommand("copy");
        ta.remove();
    }

    const handleSearchChange = async (event: ChangeEvent<{ value: unknown }>) => {
        event.preventDefault();

        if (event.target.value) {
            setSearchValue(event.target.value as string);

        } else {
            setSearchValue('');
        }
    };

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
                <Grid item xs={12} md={6}>
                    <Grid item xs={12}>
                        <Box>
                            <TypographyH1Primary
                                textAlign="center"
                                sx={{
                                    mb: 2
                                }}
                                variant="h1"
                            >
                                {('Share videos and images from any site, with anyone')}
                            </TypographyH1Primary>
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
                                            mr: 2,
                                            py: 0.5,
                                            px: 2,
                                        }}
                                    >
                                        <Button

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
                                        <IconButton sx={{ mx: 1 }} onClick={copyToClipBoard}>
                                            <ContentCopyIcon fontSize="medium" />
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