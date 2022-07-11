package co.shareleaf.instagram4j.requests.highlights;

import co.shareleaf.instagram4j.requests.IGGetRequest;
import co.shareleaf.instagram4j.responses.highlights.HighlightsUserTrayResponse;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class HighlightsUserTrayRequest extends IGGetRequest<HighlightsUserTrayResponse> {
    @NonNull
    private Long pk;

    @Override
    public String path() {
        return "highlights/" + pk + "/highlights_tray/";
    }

    @Override
    public Class<HighlightsUserTrayResponse> getResponseType() {
        return HighlightsUserTrayResponse.class;
    }

}
