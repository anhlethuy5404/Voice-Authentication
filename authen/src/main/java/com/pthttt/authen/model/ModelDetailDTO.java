package com.pthttt.authen.model;

import java.util.List;

public class ModelDetailDTO {
    Model model;
    List<Voice> voices;
    List<Hyperparameter>  hyperparameters;

    ModelDetailDTO() {}

    ModelDetailDTO(Model model, List<Voice> voices, List<Hyperparameter> hyperparameters) {
        this.model = model;
        this.voices = voices;
        this.hyperparameters = hyperparameters;
    }
}
