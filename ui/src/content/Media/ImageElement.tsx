import React, {FC} from "react";
import 'react-html5video/dist/styles.css';
import {Box, Typography} from "@mui/material";

interface ImageElementProps {
    imageSrc?: string,
    title?: string,
    handleOpen?: () => void
}
const ImageElement: FC<ImageElementProps> = (props) => {
    const handleOpen = () => {
        props.handleOpen();
    }
    return (
        <>
            <Box sx={{marginTop: '2rem', textAlign: 'center'}}>
                <img
                    onClick={handleOpen}
                    width="100%"
                    alt={props.title}
                    src={props.imageSrc} />
    </Box>
            <Box sx={{ pl: 1, pr: 1}}>
                <Typography variant="h4" sx={{
                    pt: 2,
                    pb: 2,
                    textAlign: 'left',
                    color: '#000'
                }}>
                    {props.title}
                </Typography>
            </Box>
        </>
    );
}
export default ImageElement;