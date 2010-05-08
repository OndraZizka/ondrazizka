/*    */ package com.jspbook;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import javax.servlet.Filter;
/*    */ import javax.servlet.FilterChain;
/*    */ import javax.servlet.FilterConfig;
/*    */ import javax.servlet.ServletException;
/*    */ import javax.servlet.ServletRequest;
/*    */ import javax.servlet.ServletResponse;
/*    */ import javax.servlet.http.HttpServletRequest;
/*    */ import javax.servlet.http.HttpServletResponse;
/*    */ 
/*    */ public class GZIPFilter
/*    */   implements Filter
/*    */ {
/*    */   public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
/*    */     throws IOException, ServletException
/*    */   {
/* 12 */     if (req instanceof HttpServletRequest) {
/* 13 */       HttpServletRequest request = (HttpServletRequest)req;
/* 14 */       HttpServletResponse response = (HttpServletResponse)res;
/*    */ 
/* 17 */       String ae = request.getHeader("accept-encoding");
/* 18 */       if ((ae != null) && (ae.indexOf("gzip") != -1))
/*    */       {
/* 20 */         GZIPResponseWrapper wrappedResponse = new GZIPResponseWrapper(
/* 21 */           response);
/* 22 */         chain.doFilter(req, wrappedResponse);
/* 23 */         wrappedResponse.finishResponse();
/* 24 */         return;
/*    */       }
/*    */ 
/* 27 */       chain.doFilter(req, res);
/*    */     }
/*    */   }
/*    */ 
/*    */   public void init(FilterConfig filterConfig)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void destroy()
/*    */   {
/*    */   }
/*    */ }

/* Location:           T:\_projekty\WicketTry\jspbook.jar
 * Qualified Name:     com.jspbook.GZIPFilter
 * JD-Core Version:    0.5.4
 */