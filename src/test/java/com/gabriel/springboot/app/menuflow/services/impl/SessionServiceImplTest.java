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
import com.gabriel.springboot.app.menuflow.models.entities.enums.SessionStatus;
import com.gabriel.springboot.app.menuflow.repositories.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SessionServiceImplTest {

    @Mock private TableSessionRepository sessionRepository;
    @Mock private DiningTableRepository tableRepository;
    @Mock private OrderRepository orderRepository;
    @Mock private TableMapper tableMapper;

    @InjectMocks
    private SessionServiceImpl sessionService;

    private DiningTable table;
    private TableSession session;

    @BeforeEach
    void setUp() {
        table = mock(DiningTable.class);
        lenient().when(table.isActive()).thenReturn(true);
        lenient().when(table.getNumber()).thenReturn(1);
        session = mock(TableSession.class);
    }

    // =============================
    // openSession
    // =============================

    @Test
    void openSession_shouldCreateSessionSuccessfully() {
        when(tableRepository.findById(1L)).thenReturn(Optional.of(table));
        when(sessionRepository.existsByDiningTableIdAndStatus(1L, SessionStatus.OPEN))
                .thenReturn(false);

        when(sessionRepository.save(any(TableSession.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        SessionResponse response = mock(SessionResponse.class);
        when(tableMapper.convertToSession(any())).thenReturn(response);

        SessionResponse result = sessionService.openSession(1L, null);

        assertNotNull(result);
        verify(sessionRepository).save(any(TableSession.class));
        verify(tableMapper).convertToSession(any(TableSession.class));
    }

    @Test
    void openSession_shouldThrowWhenTableNotFound() {
        when(tableRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> sessionService.openSession(1L, null));
    }

    @Test
    void openSession_shouldThrowWhenTableInactive() {
        when(tableRepository.findById(1L)).thenReturn(Optional.of(table));
        when(table.isActive()).thenReturn(false);

        assertThrows(BusinessException.class,
                () -> sessionService.openSession(1L, null));
    }

    @Test
    void openSession_shouldThrowWhenSessionAlreadyOpen() {
        when(tableRepository.findById(1L)).thenReturn(Optional.of(table));
        when(sessionRepository.existsByDiningTableIdAndStatus(1L, SessionStatus.OPEN))
                .thenReturn(true);

        assertThrows(BusinessException.class,
                () -> sessionService.openSession(1L, null));
    }

    // =============================
    // closeSession
    // =============================

    @Test
    void closeSession_shouldCloseSuccessfully() {
        CloseSessionRequest request = mock(CloseSessionRequest.class);
        when(request.sessionId()).thenReturn(1L);

        session = spy(new TableSession());
        session.open();
        session.openedAt(LocalDateTime.now());

        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(sessionRepository.save(session)).thenReturn(session);

        when(tableMapper.convertToSession(session))
                .thenReturn(mock(SessionResponse.class));

        SessionResponse result = sessionService.closeSession(request);

        assertNotNull(result);
        verify(session).close();
        verify(sessionRepository).save(session);
    }

    @Test
    void closeSession_shouldThrowWhenAlreadyClosed() {
        CloseSessionRequest request = mock(CloseSessionRequest.class);
        when(request.sessionId()).thenReturn(1L);

        session = mock(TableSession.class);
        when(session.getStatus()).thenReturn(SessionStatus.CLOSED);

        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));

        assertThrows(BusinessException.class,
                () -> sessionService.closeSession(request));
    }

    // =============================
    // getSessionById
    // =============================

    @Test
    void getSessionById_shouldReturnSession() {
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(tableMapper.convertToSession(session))
                .thenReturn(mock(SessionResponse.class));

        SessionResponse result = sessionService.getSessionById(1L);

        assertNotNull(result);
    }

    // =============================
    // getSessionDetail
    // =============================

    @Test
    void getSessionDetail_shouldReturnDetail() {
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(orderRepository.findByTableSessionId(1L))
                .thenReturn(List.of(mock(Order.class)));

        when(tableMapper.convertToDetailResponse(any(), any()))
                .thenReturn(mock(SessionDetailResponse.class));

        SessionDetailResponse result = sessionService.getSessionDetail(1L);

        assertNotNull(result);
        verify(orderRepository).findByTableSessionId(1L);
    }

    // =============================
    // getAllActiveSessions
    // =============================

    @Test
    void getAllActiveSessions_shouldReturnSortedList() {
        TableSession s1 = new TableSession();
        s1.openedAt(LocalDateTime.now().minusHours(2));

        TableSession s2 = new TableSession();
        s2.openedAt(LocalDateTime.now());

        when(sessionRepository.findByStatus(SessionStatus.OPEN))
                .thenReturn(List.of(s2, s1));

        when(tableMapper.convertToSession(any()))
                .thenReturn(mock(SessionResponse.class));

        List<SessionResponse> result = sessionService.getAllActiveSessions();

        assertEquals(2, result.size());
        verify(sessionRepository).findByStatus(SessionStatus.OPEN);
    }

    // =============================
    // getActivateSessionByTable
    // =============================

    @Test
    void getActivateSessionByTable_shouldReturnSession() {
        when(sessionRepository.findByDiningTableIdAndStatus(1L, SessionStatus.OPEN))
                .thenReturn(Optional.of(session));

        when(tableMapper.convertToSession(session))
                .thenReturn(mock(SessionResponse.class));

        SessionResponse result = sessionService.getActivateSessionByTable(1L);

        assertNotNull(result);
    }
}