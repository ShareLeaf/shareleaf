import React, { FC } from "react";
import { DefaultPlayer as Video } from 'react-html5video';
import 'react-html5video/dist/styles.css';
import {Box, Typography} from "@mui/material";

interface VideoElementProps {
    encoding: string
    src: string,
    caption?: string
}
const VideoElement: FC<VideoElementProps> = (props) => {
    return (
        <Box sx={{marginTop: '2rem'}}>
            <Video autoPlay loop muted
                   controls={['PlayPause', 'Seek', 'Time', 'Volume', 'Fullscreen']}
                   onCanPlayThrough={() => {
                       // Do stuff
                   }}>
                <source src={props.src} type={props.encoding} />
            </Video>
            <Box sx={{
                marginTop: '0.5rem'
            }}>
            <Typography
                sx={{
                    pt: 1
                }}
                variant="h4"
            >
                {props.caption}
            </Typography>
            </Box>
        </Box>
    );
}
export default VideoElement;