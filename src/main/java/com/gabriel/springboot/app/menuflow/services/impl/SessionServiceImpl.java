package com.gabriel.springboot.app.menuflow.services.impl;

import com.gabriel.springboot.app.menuflow.exceptions.BusinessException;
import com.gabriel.springboot.app.menuflow.exceptions.ResourceNotFoundException;
import com.gabriel.springboot.app.menuflow.mappers.TableMapper;
import com.gabriel.springboot.app.menuflow.models.dto.request.CloseSessionRequest;
import com.gabriel.springboot.app.menuflow.models.dto.response.SessionDetailResponse;
import com.gabriel.springboot.app.menuflow.models.dto.response.SessionResponse;
import com.gabriel.springboot.app.menuflow.models.entities.DiningTable;
import com.gabriel.springboot.app.menuflow.models.entities.Order;
import com.gabriel.springboot.app.menuflow.models.entities.TableSession;
import com.gabriel.springboot.app.menuflow.models.entities.User;
import com.gabriel.springboot.app.menuflow.models.entities.enums.SessionStatus;
import com.gabriel.springboot.app.menuflow.repositories.DiningTableRepository;
import com.gabriel.springboot.app.menuflow.repositories.OrderRepository;
import com.gabriel.springboot.app.menuflow.repositories.TableSessionRepository;
import com.gabriel.springboot.app.menuflow.repositories.UserRepository;
import com.gabriel.springboot.app.menuflow.services.SessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {

    private final TableSessionRepository sessionRepository;
    private final DiningTableRepository tableRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final TableMapper tableMapper;

    /**
     * Opens a new session for a table.
     *
     * @param tableId Table ID
     * @param openedBy ID of the user opening the session (can be null)
     * @return The created session
     * @throws ResourceNotFoundException if the table does not exist
     * @throws BusinessException if the table is inactive or already has an open session
     */
    @Override
    @Transactional
    public SessionResponse openSession(Long tableId, Long openedBy) {

        DiningTable table = tableRepository.findById(tableId)
                .orElseThrow(() -> new ResourceNotFoundException("table not found"));

        if (!table.isActive()){
            throw new BusinessException("You cannot log in to an inactive table");
        }

        if (sessionRepository.existsByDiningTableIdAndStatus(tableId, SessionStatus.OPEN)){
            throw new BusinessException("Table already exists a session active");
        }

        TableSession session = new TableSession();
        session.assignTable(table);
        session.openedAt(LocalDateTime.now());
        session.open();

        if (openedBy != null){
            User user = userRepository.findById(openedBy)
                    .orElse(null);
            session.openedBy(user);
        }

        TableSession saved = sessionRepository.save(session);

        log.info("Open session - ID: {}, Table: {}", saved.getId(), table.getNumber());

        return tableMapper.convertToSession(saved);
    }

    @Override
    @Transactional
    public SessionResponse closeSession(CloseSessionRequest request) {

        log.info("Logout ID: {}", request.sessionId());

        TableSession session = sessionRepository.findById(request.sessionId())
                .orElseThrow(() -> new ResourceNotFoundException("session not found"));

        if(session.getStatus().equals(SessionStatus.CLOSED)){
            throw new BusinessException("The session has now ended");
        }

        session.close();

        TableSession closed = sessionRepository.save(session);

        log.info("Closed session - ID: {}, Duration: {}",
                closed.getId(), tableMapper.calculateDuration(closed.getOpenedAt(), closed.getClosedAt()));

        return tableMapper.convertToSession(closed);
    }

    @Override
    @Transactional(readOnly = true)
    public SessionResponse getSessionById(Long sessionId) {
        TableSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("session not found"));

        return tableMapper.convertToSession(session);
    }

    @Override
    @Transactional(readOnly = true)
    public SessionDetailResponse getSessionDetail(Long sessionId) {
        TableSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("session not found"));

        List<Order> orders = orderRepository.findByTableSessionId(sessionId);

        return tableMapper.convertToDetailResponse(session, orders);
    }

    @Override
    @Transactional(readOnly = true)
    public SessionResponse getActivateSessionByTable(Long tableId) {

        return sessionRepository.findByDiningTableIdAndStatus(tableId, SessionStatus.OPEN)
                .map(tableMapper::convertToSession)
                .orElseThrow(() -> new ResourceNotFoundException("table not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SessionResponse> getAllActiveSessions() {
        return sessionRepository.findByStatus(SessionStatus.OPEN).stream()
                .sorted(Comparator.comparing(TableSession::getOpenedAt))
                .map(tableMapper::convertToSession)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SessionResponse> getSessionsByTable(Long tableId) {
        return sessionRepository.findByDiningTableId(tableId).stream()
                .sorted(Comparator.comparing(TableSession::getOpenedAt))
                .map(tableMapper::convertToSession)
                .toList();
    }
}
