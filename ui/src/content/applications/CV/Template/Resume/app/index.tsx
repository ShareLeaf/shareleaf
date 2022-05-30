import type {TechProfile, TechResume } from '../types'
import {PDFViewer} from "@react-pdf/renderer";
import {FC} from "react";
import PDF from "./PDF";
import {observer} from "mobx-react";
import {useStore} from "../../../../../../hooks/useStore";

export interface AppDocumentProps {
    techProfile: TechProfile
    techResume: TechResume,
    reRenderPdf: number
}

const AppDocument: FC<AppDocumentProps> = observer(({ techProfile, techResume, reRenderPdf}) => {
    const store = useStore();

    if (store && store.documentStore &&
        store.documentStore.content &&
        reRenderPdf) {
        return (
            <div className="pdf-viewer" style={{flexGrow: 1}}>
                <PDFViewer
                    className="pdf-viewer-component"
                    showToolbar={false}
                    style={{
                        width: '100%',
                        height: '100%'
                    }}
                >
                    <PDF
                        techProfile={techProfile}
                        techResume={techResume}
                        resume={store.documentStore.content}
                    />
                </PDFViewer>
            </div>

        )
    }
    return <></>
})
export default AppDocument;
