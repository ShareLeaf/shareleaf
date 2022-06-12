import React, {FC, useEffect, useRef} from "react";
import {Box, Typography} from "@mui/material";

interface VideoElementProps {
    encoding: string
    src: string,
    title?: string,
    thumbnail?: string
}
const VideoElement: FC<VideoElementProps> = (props) => {
    const videoRef = useRef(undefined);
    useEffect(() => {
        videoRef.current.defaultMuted = true;
    })

    return (
        <Box sx={{marginTop: '2rem'}}>
            <video
                ref={videoRef}
                className="sl-video-player"
                controls
                autoPlay
                loop
                playsInline
                muted
                width={"100%"}>
                preload="metadata"
                <source src={props.src + "#t=0.1"} type="video/mp4"/>
            </video>
            <Box sx={{
                pl: 1,
                pr: 1
            }}>
            <Typography
                sx={{
                    pt: 2,
                    pb: 2,
                    textAlign: 'left',
                    color: '#000',
                }}
                variant="h4"
            >
                {props.title}
            </Typography>
            </Box>
        </Box>
    );
}
export default VideoElement;
