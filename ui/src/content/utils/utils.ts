export const copyToClipBoard = async (value: string) : Promise<void> => {
    const ta = document.createElement("textarea");
    ta.innerText = value;
    document.body.appendChild(ta);
    ta.select();
    document.execCommand("copy");
    ta.remove();
}