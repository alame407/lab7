package com.alame.lab7.server.servers;

import com.alame.lab7.common.data.StudyGroup;
import com.alame.lab7.common.exceptions.IncorrectElementFieldException;
import com.alame.lab7.common.request.ClientServerInterface;
import java.io.IOException;
import java.util.Map;

/**
 * interface server for a server project
 */
public interface ServerInterface extends ClientServerInterface {
    void putAll(Map<String, StudyGroup> studyGroupMap) throws IncorrectElementFieldException;
}
