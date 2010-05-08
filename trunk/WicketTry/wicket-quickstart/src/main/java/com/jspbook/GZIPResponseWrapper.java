/*    */ package com.jspbook;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.OutputStreamWriter;
/*    */ import java.io.PrintWriter;
/*    */ import javax.servlet.ServletOutputStream;
/*    */ import javax.servlet.http.HttpServletResponse;
/*    */ import javax.servlet.http.HttpServletResponseWrapper;
/*    */ 
/*    */ public class GZIPResponseWrapper extends HttpServletResponseWrapper
/*    */ {
/*  9 */   protected HttpServletResponse origResponse = null;
/* 10 */   protected ServletOutputStream stream = null;
/* 11 */   protected PrintWriter writer = null;
/*    */ 
/*    */   public GZIPResponseWrapper(HttpServletResponse response) {
/* 14 */     super(response);
/* 15 */     this.origResponse = response;
/*    */   }
/*    */ 
/*    */   public ServletOutputStream createOutputStream() throws IOException {
/* 19 */     return new GZIPResponseStream(this.origResponse);
/*    */   }
/*    */ 
/*    */   public void finishResponse() {
/*    */     try {
/* 24 */       if (this.writer != null) {
/* 25 */         this.writer.close();
/*    */       }
/* 27 */       else if (this.stream != null)
/* 28 */         this.stream.close();
/*    */     }
/*    */     catch (IOException localIOException) {
/*    */     }
/*    */   }
/*    */ 
/*    */   public void flushBuffer() throws IOException {
/* 35 */     this.stream.flush();
/*    */   }
/*    */ 
/*    */   public ServletOutputStream getOutputStream() throws IOException {
/* 39 */     if (this.writer != null) {
/* 40 */       throw new IllegalStateException("getWriter() has already been called!");
/*    */     }
/*    */ 
/* 43 */     if (this.stream == null)
/* 44 */       this.stream = createOutputStream();
/* 45 */     return this.stream;
/*    */   }
/*    */ 
/*    */   public PrintWriter getWriter() throws IOException {
/* 49 */     if (this.writer != null) {
/* 50 */       return this.writer;
/*    */     }
/*    */ 
/* 53 */     if (this.stream != null) {
/* 54 */       throw new IllegalStateException("getOutputStream() has already been called!");
/*    */     }
/*    */ 
/* 57 */     this.stream = createOutputStream();
/*    */ 
/* 59 */     this.writer = new PrintWriter(new OutputStreamWriter(this.stream, this.origResponse.getCharacterEncoding()));
/* 60 */     return this.writer;
/*    */   }
/*    */ 
/*    */   public void setContentLength(int length)
/*    */   {
/*    */   }
/*    */ }

/* Location:           T:\_projekty\WicketTry\jspbook.jar
 * Qualified Name:     com.jspbook.GZIPResponseWrapper
 * JD-Core Version:    0.5.4
 */