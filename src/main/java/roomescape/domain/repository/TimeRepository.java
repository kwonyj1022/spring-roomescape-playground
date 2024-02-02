package roomescape.domain.repository;

import roomescape.domain.Time;

import java.util.List;

public interface TimeRepository {

    Time save(final Time time);

    List<Time> findAll();
}
