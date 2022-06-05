import React, { FC } from "react";
import {Box, Typography} from "@mui/material";

interface VideoElementProps {
    encoding: string
    src: string,
    title?: string
}
const VideoElement: FC<VideoElementProps> = (props) => {
    return (

        <Box sx={{marginTop: '2rem'}}>

            <video controls autoPlay loop muted width={"100%"} playsInline>
                <source src={props.src} type={props.encoding}/>
            </video>
            <Box sx={{
                marginTop: '0.5rem'
            }}>
            <Typography
                sx={{
                    pt: 1,
                    textAlign: 'left'
                }}
                variant="h3"
            >
                {props.title}
            </Typography>
            </Box>
        </Box>
    );
}
export default VideoElement;
