package roomescape;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Controller
public class ReservationController {

    private List<Reservation> reservations = new ArrayList<>();
    private AtomicLong index = new AtomicLong(1);

    @GetMapping("/reservation")
    public String reservation() {
        return "reservation.html";
    }

    @GetMapping("/reservations")
    public ResponseEntity<List<Reservation>> readAll() { // Reservation 도메인을 그대로 사용하는 대신 dto를 사용할 수도 있다.
        return ResponseEntity.ok(reservations);
    }

    @PostMapping("/reservations")
    public ResponseEntity<Reservation> create(@RequestBody Reservation request) { // 요청과 응답에 Reservation 도메인 대신 dto를 사용할 수도 있다.
        // 또는 spring validation을 사용해서 검증을 수행할 수도 있다.
        if (request.getName() == null || request.getName().isBlank() ||
                request.getDate() == null || request.getDate().isBlank() ||
                request.getTime() == null || request.getTime().isBlank()) {
            throw new IllegalArgumentException("데이터가 비어있습니다.");
        }

        Reservation newReservation
                = new Reservation(index.getAndIncrement(), request.getName(), request.getDate(), request.getTime());

        reservations.add(newReservation); // 데이터베이스에 저장

        return ResponseEntity.created(URI.create("/reservations/" + newReservation.getId()))
                             .body(newReservation);
    }

    @DeleteMapping("/reservations/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boolean flag = false;
        for (int i = 0; i < reservations.size(); i++) {
            if (reservations.get(i).getId().equals(id)) {
                flag = true;
                reservations.remove(reservations.get(i));
            }
        }
        if (!flag) {
            throw new IllegalArgumentException("예약이 존재하지 않습니다.");
        }

        // 또는 아래처럼 stream을 사용할 수도 있음
//        Reservation target = reservations.stream()
//                                         .filter(reservation -> Objects.equals(reservation.getId(), id))
//                                         .findFirst()
//                                         .orElseThrow(() -> new IllegalArgumentException("예약이 존재하지 않습니다."));
//        reservations.remove(target);

        return ResponseEntity.noContent().build();
    }

    // IllegalArgumentException 대신 커스텀 예외를 만들어서 처리할 수도 있다.
    // ControllerAdvice 어노테이션을 사용해서 전역적으로 예외를 관리할 수도 있다.
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Void> handleException(IllegalArgumentException e) {
        return ResponseEntity.badRequest().build();
    }
}
