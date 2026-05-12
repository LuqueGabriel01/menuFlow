package com.gabriel.springboot.app.menuflow.services;

import com.gabriel.springboot.app.menuflow.models.dto.request.table.CloseSessionRequest;
import com.gabriel.springboot.app.menuflow.models.dto.response.table.SessionDetailResponse;
import com.gabriel.springboot.app.menuflow.models.dto.response.table.SessionResponse;

import java.util.List;

public interface SessionService {
    SessionResponse openSession(Long tableId, Long openedBy);
    SessionResponse closeSession(CloseSessionRequest request);
    SessionResponse getSessionById(Long sessionId);
    SessionDetailResponse getSessionDetail(Long sessionId);
    SessionResponse getActivateSessionByTable(Long tableId);
    List<SessionResponse> getAllActiveSessions();
    List<SessionResponse> getSessionsByTable(Long tableId);
}
