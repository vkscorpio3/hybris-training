package org.razorfish.storefront.filters;

import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.servicelayer.web.SessionFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class WebSessionFilter extends SessionFilter{
    @Override
    protected void activateSession(HttpSession httpSession, HttpServletRequest request) {
        super.activateSession(httpSession,request);
        synchronized(httpSession) {
            JaloSession jalosession = (JaloSession)httpSession.getAttribute("jalosession");
            httpSession.setAttribute("jalosession",jalosession);
        }
    }
}
