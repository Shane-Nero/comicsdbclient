package cz.kutner.comicsdb.event;

import java.util.List;

import cz.kutner.comicsdb.model.Classified;
import cz.kutner.comicsdb.model.ForumEntry;

/**
 * Created by Lukáš Kutner (lukas@kutner.cz) on 21.5.2015.
 */
public class ClassifiedResultEvent {
    private List<Classified> result;

    public ClassifiedResultEvent(List<Classified> result) {
        this.result = result;
    }

    public List<Classified> getResult() {
        return result;
    }
}