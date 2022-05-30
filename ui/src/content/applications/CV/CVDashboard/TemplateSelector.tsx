import {
    alpha,
    Box, Button,
    Card,
    CardActionArea,
    CardHeader,
    CardMedia,
    Link,
    styled,
    Typography,
    useTheme
} from '@mui/material';
import SwiperCore, {Navigation, Pagination} from 'swiper';
import {RRCVTemplate} from "../../../../api";
import {FC} from "react";

SwiperCore.use([Navigation, Pagination]);

const CardActionAreaWrapper = styled(CardActionArea)(
  () => `
  display: flex;
  align-items: center;
  justify-content: space-between;
  position: relative;

  .MuiTouchRipple-root {
      opacity: .3;
  }

  &:hover {
      .MuiCardActionArea-focusHighlight {
          opacity: .05;
      }
  }
`
);
interface TemplateSelectorProps {
    onDocumentCreate: (templateType: RRCVTemplate) => void
}

const TemplateSelector: FC<TemplateSelectorProps> = (props) =>{
    const theme = useTheme();

    const handleSelection = (templateType: RRCVTemplate) : void => {
        props.onDocumentCreate(templateType);
    }

    return (
        <Card sx={{background: 'none', boxShadow: 'none'}}>
            <CardHeader
                disableTypography
                sx={{
                    p: 2
                }}
                title={
                    <>
                        <Typography
                            component="div"
                            sx={{
                                fontSize: `${theme.typography.pxToRem(17)}`
                            }}
                            textAlign="center"
                            gutterBottom
                            variant="h3"
                        >
                            Create from a template
                        </Typography>
                    </>
                }
            />
            <Box
                pb={3}
                display="flex"
                alignItems="center"
                justifyContent="center"
                flexWrap="wrap"
            >
                <Box pl={4} py={0}
                     onClick={() => handleSelection(RRCVTemplate.PROFESSIONAL)}>
                    <Card
                        sx={{
                            textAlign: 'center',
                            transition: `${theme.transitions.create([
                                'box-shadow',
                                'transform'
                            ])}`,
                            transform: 'translateY(0px)',

                            '&:hover': {
                                transform: 'translateY(-10px)',
                                boxShadow: `0 2rem 8rem 0 ${alpha(
                                    theme.colors.alpha.black[100],
                                    0.1
                                )}, 
                                0 0.6rem 1.6rem ${alpha(
                                    theme.colors.alpha.black[100],
                                    0.2
                                )}, 
                                0 0.2rem 0.2rem ${alpha(
                                    theme.colors.alpha.black[100],
                                    0.15
                                )}`
                            }
                        }}
                    >
                        <CardActionAreaWrapper>
                            <CardMedia
                                component="img"
                                height="230"
                                image="/static/images/templates/Professional.png"
                                alt="..."
                            />
                        </CardActionAreaWrapper>
                    </Card>
                    <Box
                        sx={{
                            px: { md: 2, lg: 1.5, xl: 3 },
                            pt: 2,
                            textAlign: 'center'
                        }}
                    >
                        <Button onClick={() => handleSelection(RRCVTemplate.PROFESSIONAL)}>
                            Professional
                        </Button>
                        {/*<Link*/}
                        {/*    onClick={() => handleSelection(RRCVTemplate.PROFESSIONAL)}*/}
                        {/*    lineHeight={1.5}*/}
                        {/*    sx={{*/}
                        {/*        transition: `${theme.transitions.create(['color'])}`,*/}
                        {/*        color: `${theme.colors.alpha.black[100]}`,*/}

                        {/*        '&:hover': {*/}
                        {/*            color: `${theme.colors.primary.main}`*/}
                        {/*        }*/}
                        {/*    }}*/}
                        {/*    color="text.primary"*/}
                        {/*    variant="h5"*/}
                        {/*    underline="none"*/}
                        {/*>*/}
                        {/*    */}
                        {/*</Link>*/}
                    </Box>
                </Box>
                <Box pl={4} py={0}>
                    <Card
                        sx={{
                            textAlign: 'center',
                            transition: `${theme.transitions.create([
                                'box-shadow',
                                'transform'
                            ])}`,
                            transform: 'translateY(0px)',

                            '&:hover': {
                                transform: 'translateY(-10px)',
                                boxShadow: `0 2rem 8rem 0 ${alpha(
                                    theme.colors.alpha.black[100],
                                    0.1
                                )}, 
                                0 0.6rem 1.6rem ${alpha(
                                    theme.colors.alpha.black[100],
                                    0.2
                                )}, 
                                0 0.2rem 0.2rem ${alpha(
                                    theme.colors.alpha.black[100],
                                    0.15
                                )}`
                            }
                        }}
                    >
                        <CardActionAreaWrapper>
                            <CardMedia
                                component="img"
                                height="230"
                                image="/static/images/templates/Modern.png"
                                alt="..."
                            />
                        </CardActionAreaWrapper>
                    </Card>
                    <Box
                        sx={{
                            px: { md: 2, lg: 1.5, xl: 3 },
                            pt: 2,
                            textAlign: 'center'
                        }}
                    >
                        <Link
                            lineHeight={1.5}
                            href="#"
                            sx={{
                                transition: `${theme.transitions.create(['color'])}`,
                                color: `${theme.colors.alpha.black[100]}`,

                                '&:hover': {
                                    color: `${theme.colors.primary.main}`
                                }
                            }}
                            color="text.primary"
                            variant="h5"
                            underline="none"
                        >
                            Modern
                        </Link>
                    </Box>
                </Box>
                <Box pl={4} py={0}>
                    <Card
                        sx={{
                            textAlign: 'center',
                            transition: `${theme.transitions.create([
                                'box-shadow',
                                'transform'
                            ])}`,
                            transform: 'translateY(0px)',

                            '&:hover': {
                                transform: 'translateY(-10px)',
                                boxShadow: `0 2rem 8rem 0 ${alpha(
                                    theme.colors.alpha.black[100],
                                    0.1
                                )}, 
                                0 0.6rem 1.6rem ${alpha(
                                    theme.colors.alpha.black[100],
                                    0.2
                                )}, 
                                0 0.2rem 0.2rem ${alpha(
                                    theme.colors.alpha.black[100],
                                    0.15
                                )}`
                            }
                        }}
                    >
                        <CardActionAreaWrapper>
                            <CardMedia
                                component="img"
                                height="230"
                                image="/static/images/templates/Classic.png"
                                alt="..."
                            />
                        </CardActionAreaWrapper>
                    </Card>
                    <Box
                        sx={{
                            px: { md: 2, lg: 1.5, xl: 3 },
                            pt: 2,
                            textAlign: 'center'
                        }}
                    >
                        <Link
                            lineHeight={1.5}
                            href="#"
                            sx={{
                                transition: `${theme.transitions.create(['color'])}`,
                                color: `${theme.colors.alpha.black[100]}`,

                                '&:hover': {
                                    color: `${theme.colors.primary.main}`
                                }
                            }}
                            color="text.primary"
                            variant="h5"
                            underline="none"
                        >
                            Classic
                        </Link>
                    </Box>
                </Box>
            </Box>
        </Card>
    );
}

export default TemplateSelector;
