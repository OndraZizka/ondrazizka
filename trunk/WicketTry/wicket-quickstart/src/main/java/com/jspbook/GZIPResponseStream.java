/*     */ package com.jspbook;
/*     */ 
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.util.zip.GZIPOutputStream;
/*     */ import javax.servlet.ServletOutputStream;
/*     */ import javax.servlet.http.HttpServletResponse;
/*     */ 
/*     */ public class GZIPResponseStream extends ServletOutputStream
/*     */ {
/*  14 */   protected OutputStream bufferedOutput = null;
/*     */ 
/*  17 */   protected boolean closed = false;
/*     */ 
/*  20 */   protected HttpServletResponse response = null;
/*     */ 
/*  23 */   protected ServletOutputStream output = null;
/*     */ 
/*  26 */   private int bufferSize = 50000;
/*     */ 
/*     */   public GZIPResponseStream(HttpServletResponse response) throws IOException
/*     */   {
/*  30 */     this.closed = false;
/*  31 */     this.response = response;
/*  32 */     this.output = response.getOutputStream();
/*  33 */     this.bufferedOutput = new ByteArrayOutputStream();
/*     */   }
/*     */ 
/*     */   public void close() throws IOException
/*     */   {
/*  38 */     if (this.bufferedOutput instanceof ByteArrayOutputStream)
/*     */     {
/*  40 */       ByteArrayOutputStream baos = (ByteArrayOutputStream)this.bufferedOutput;
/*     */ 
/*  43 */       ByteArrayOutputStream compressedContent = new ByteArrayOutputStream();
/*  44 */       GZIPOutputStream gzipstream = new GZIPOutputStream(
/*  45 */         compressedContent);
/*  46 */       byte[] bytes = baos.toByteArray();
/*  47 */       gzipstream.write(bytes);
/*  48 */       gzipstream.finish();
/*     */ 
/*  51 */       byte[] compressedBytes = compressedContent.toByteArray();
/*     */ 
/*  54 */       this.response.setContentLength(compressedBytes.length);
/*  55 */       this.response.addHeader("Content-Encoding", "gzip");
/*  56 */       this.output.write(compressedBytes);
/*  57 */       this.output.flush();
/*  58 */       this.output.close();
/*  59 */       this.closed = true;
/*     */     }
/*     */     else
/*     */     {
/*  64 */       if ( ! ( this.bufferedOutput instanceof GZIPOutputStream) )
/*     */         return;
/*  66 */       GZIPOutputStream gzipstream = (GZIPOutputStream)this.bufferedOutput;
/*     */ 
/*  68 */       gzipstream.finish();
/*     */ 
/*  70 */       this.output.flush();
/*  71 */       this.output.close();
/*  72 */       this.closed = true;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void flush() throws IOException {
/*  77 */     if (this.closed) {
/*  78 */       throw new IOException("Cannot flush a closed output stream");
/*     */     }
/*  80 */     this.bufferedOutput.flush();
/*     */   }
/*     */ 
/*     */   public void write(int b) throws IOException {
/*  84 */     if (this.closed) {
/*  85 */       throw new IOException("Cannot write to a closed output stream");
/*     */     }
/*     */ 
/*  88 */     checkBufferSize(1);
/*     */ 
/*  90 */     this.bufferedOutput.write((byte)b);
/*     */   }
/*     */ 
/*     */   private void checkBufferSize(int length) throws IOException
/*     */   {
/*  95 */     if (this.bufferedOutput instanceof ByteArrayOutputStream) {
/*  96 */       ByteArrayOutputStream baos = (ByteArrayOutputStream)this.bufferedOutput;
/*  97 */       if (baos.size() + length <= this.bufferSize) {
/*     */         return;
/*     */       }
/* 100 */       this.response.addHeader("Content-Encoding", "gzip");
/*     */ 
/* 102 */       byte[] bytes = baos.toByteArray();
/*     */ 
/* 104 */       GZIPOutputStream gzipstream = new GZIPOutputStream(this.output);
/* 105 */       gzipstream.write(bytes);
/*     */ 
/* 107 */       this.bufferedOutput = gzipstream;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void write(byte[] b) throws IOException
/*     */   {
/* 113 */     write(b, 0, b.length);
/*     */   }
/*     */ 
/*     */   public void write(byte[] b, int off, int len) throws IOException {
/* 117 */     System.out.println("writing...");
/* 118 */     if (this.closed) {
/* 119 */       throw new IOException("Cannot write to a closed output stream");
/*     */     }
/*     */ 
/* 122 */     checkBufferSize(len);
/*     */ 
/* 124 */     this.bufferedOutput.write(b, off, len);
/*     */   }
/*     */ 
/*     */   public boolean closed() {
/* 128 */     return this.closed;
/*     */   }
/*     */ 
/*     */   public void reset()
/*     */   {
/*     */   }
/*     */ }

/* Location:           T:\_projekty\WicketTry\jspbook.jar
 * Qualified Name:     com.jspbook.GZIPResponseStream
 * JD-Core Version:    0.5.4
 */