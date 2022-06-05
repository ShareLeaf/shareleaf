import React, { FC } from "react";
import 'react-html5video/dist/styles.css';
import {Box, Typography} from "@mui/material";

interface ImageElementProps {
    src: string,
    title?: string
}
const ImageElement: FC<ImageElementProps> = (props) => {
    return (
        <>
            <Box sx={{marginTop: '2rem', textAlign: 'center'}}>
        <img
            width="100%"
            alt={props.title}
            src={props.src} />
    </Box>
            <Box sx={{marginTop: '0.5rem'}}>
                <Typography variant="h3" sx={{textAlign: 'left'}}>
                    {props.title}
                </Typography>
            </Box>
        </>
    );
}
export default ImageElement;