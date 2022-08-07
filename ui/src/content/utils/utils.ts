import {ContentApi} from "@/api";
import {headerConfig} from "@/api/headerConfig";

export const copyToClipBoard = async (value: string) : Promise<void> => {
    const ta = document.createElement("textarea");
    ta.innerText = value;
    document.body.appendChild(ta);
    ta.select();
    document.execCommand("copy");
    ta.remove();
}

export const handleShareClick = (url): void => {
    const contentId = url.split(".co/")[1];
    new ContentApi(headerConfig()).incrementShareCount({uid: contentId}).then(_ => {
    }).catch(e => console.log(e))
}