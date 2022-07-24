import React, {FC, useRef} from "react";
import {Box, Typography} from "@mui/material";
import VideoJS from './VideoJS'
import {VideoJsPlayer} from 'video.js';

interface VideoElementProps {
    encoding: string
    videoSrc: string,
    audioSrc?: string,
    title?: string,
    thumbnail?: string
}
const VideoElement: FC<VideoElementProps> = (props) => {
    const playerRef = useRef<VideoJsPlayer>(null);
    const videoJsOptions = {
        loop: true,
        autoplay: true,
        controls: true,
        responsive: true,
        fluid: true,
        muted: true,
        preload: "metadata",

        // disable default iOS and Android native controls so we can bind the audio sync
        // actions
        // nativeControlsForTouch: true,
        sources: [{
            src: props.videoSrc,
            type: 'application/x-mpegURL'
        }],
        controlBar: {
            playToggle: true,
            pictureInPictureToggle: false,
            progressControl: true,
            fullscreenToggle: true,
            volumePanel: true,
            remainingTimeDisplay: true,
        },
        overrideNative: true,
        nativeAudioTracks: false,
        nativeTextTracks: false
    };

    const handlePlayerReady = (player: VideoJsPlayer) => {
        playerRef.current = player;
    };

    return (
        <Box sx={{width: '100%'}}>
            <VideoJS options={videoJsOptions} onReady={handlePlayerReady} audioSrc={props.audioSrc}/>
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


