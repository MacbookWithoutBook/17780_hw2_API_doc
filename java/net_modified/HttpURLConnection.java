/*
 * Copyright (c) 1996, 2023, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package java.net_modified;

import java.io.InputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.Permission;
import java.util.Date;
import java.net.*;

/**
 * This class represents an HTTP-specific implementation of a {@link URLConnection} 
 * that provides methods for interacting with resources via HTTP. It supports features 
 * such as handling HTTP requests and responses. 
 * 
 * <p>Each {@code HttpURLConnection} instance is designed to handle a single HTTP request, 
 * although the underlying network connection to the HTTP server may be reused by 
 * other instances. Closing the InputStream or OutputStream associated 
 * with the {@code HttpURLConnection} may release network resources, but will not 
 * affect shared persistent connections. Calling {@link #disconnect()} may close 
 * the underlying socket if no other connections are using it.
 *
 * <p>The behavior of HTTP connections can be controlled via system properties, 
 * such as proxy settings and miscellaneous HTTP settings.
 * 
 * <h3>Constructor Usage</h3>
 * <p>{@code HttpURLConnection} is an abstract class, and instances of this class 
 * are typically obtained by calling {@link URL#openConnection()} on a {@code URL} 
 * object that uses the HTTP protocol. For example:</p>
 * 
 * <pre>
 * URL url = new URL("http://www.example.com");
 * HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
 * </pre>
 * 
 * <p>This will create an instance of a concrete subclass of {@code HttpURLConnection}, 
 * depending on the protocol handler available in the environment.</p>
 * 
 * <h3>Security Considerations</h3>
 * <p>If a security manager is installed, the caller must have the appropriate permissions 
 * to open a connection. The caller needs either:</p>
 * <ul>
 *   <li>A "connect" {@link SocketPermission} for the host/port combination of the 
 *       destination URL.</li>
 *   <li>A {@link URLPermission} that allows the request.</li>
 * </ul>
 * 
 * <p>If automatic redirection is enabled, the caller must also possess the necessary 
 * permissions to connect to the redirected host/URL.</p>
 * 
 * <p><b>Note:</b> This class is not thread-safe. Each instance should be used by 
 * one thread at a time.</p>
 * 
 * @see     java.net.HttpURLConnection#disconnect()
 * @since   1.1
 */

public abstract class HttpURLConnection extends URLConnection {
    /* instance variables */

     /**
     * The HTTP method used for the request (e.g., "GET", "POST", "PUT", etc.).
     * 
     * <p>By default, this is set to "GET". This field determines which HTTP method 
     * will be sent in the request. Valid values include standard HTTP methods such as 
     * "GET", "POST", "PUT", and "DELETE". Other custom methods may also be supported 
     * by the server.
     *
     * <p><b>Note:</b> The method must be set before establishing the connection. 
     * After the request is sent, modifying this value will have no effect.
     *
     * @see #setRequestMethod(String)
     */
    protected String method = "GET";

    /**
     * The chunk-length to be used when the request body is sent using chunked encoding.
     * 
     * <p>A value of {@code -1} indicates that chunked encoding is disabled. When chunked 
     * encoding is enabled, the request body is split into smaller chunks, and each chunk 
     * is sent separately. This allows the body to be sent without knowing its size 
     * beforehand.
     *
     * <p><b>Important:</b> This value must be set before the request is initiated, 
     * and changing it after the request is started will have no effect.
     *
     * <p>Use this option for sending large bodies where the size is not known in advance.
     *
     * @since 1.5
     * @see #setChunkedStreamingMode(int)
     */
    protected int chunkLength = -1;

    /**
     * The fixed content-length in bytes for output when using fixed-length streaming mode.
     * 
     * <p>A value of {@code -1} means that fixed-length streaming mode is disabled. 
     * When enabled, the client sends the exact number of bytes specified. This can 
     * be more efficient when the size of the request body is known in advance.
     *
     * <p><b>Note:</b> It is recommended to use {@link #fixedContentLengthLong} instead 
     * of this field, as it allows for larger content lengths to be set. Changing this 
     * field after initiating the connection will not affect the current request.
     *
     * @since 1.5
     * @see #setFixedLengthStreamingMode(int)
     */
    protected int fixedContentLength = -1;

    /**
     * The fixed content-length in bytes for output when using fixed-length streaming mode.
     * 
     * <p>A value of {@code -1} means that fixed-length streaming mode is disabled. 
     * This field supports content lengths larger than those allowed by {@code fixedContentLength}.
     * When enabled, the client sends the exact number of bytes specified in the request.
     *
     * <p>Using fixed-length streaming can improve performance by allowing the connection 
     * to be reused, but it requires knowing the size of the request body in advance.
     *
     * @since 1.7
     * @see #setFixedLengthStreamingMode(long)
     */
    protected long fixedContentLengthLong = -1;

    /**
     * Supplies an {@link java.net.Authenticator Authenticator} to be used
    * when authentication is requested through the HTTP protocol for
    * this {@code HttpURLConnection}.
    * If no authenticator is supplied, the
    * {@linkplain Authenticator#setDefault(java.net.Authenticator) default
    * authenticator} will be used.
    *
    * @implSpec The default behavior of this method is to unconditionally
    *           throw {@link UnsupportedOperationException}. Concrete
    *           implementations of {@code HttpURLConnection}
    *           which support supplying an {@code Authenticator} for a
    *           specific {@code HttpURLConnection} instance should
    *           override this method to implement a different behavior.
    *
    * @implNote Depending on authentication schemes, an implementation
    *           may or may not need to use the provided authenticator
    *           to obtain a password. For instance, an implementation that
    *           relies on third-party security libraries may still invoke the
    *           default authenticator if these libraries are configured
    *           to do so.
    *           Likewise, an implementation that supports transparent
    *           NTLM authentication may let the system attempt
    *           to connect using the system user credentials first,
    *           before invoking the provided authenticator.
    *           <br>
    *           However, if an authenticator is specifically provided,
    *           then the underlying connection may only be reused for
    *           {@code HttpURLConnection} instances which share the same
    *           {@code Authenticator} instance, and authentication information,
    *           if cached, may only be reused for an {@code HttpURLConnection}
    *           sharing that same {@code Authenticator}.
    *
    * @param auth The {@code Authenticator} that should be used by this
    *           {@code HttpURLConnection}.
    *
    * @throws  UnsupportedOperationException if setting an Authenticator is
    *          not supported by the underlying implementation.
    * @throws  IllegalStateException if URLConnection is already connected.
    * @throws  NullPointerException if the supplied {@code auth} is {@code null}.
    * @since 9
    */
    public void setAuthenticator(Authenticator auth) {
        throw new UnsupportedOperationException("Supplying an authenticator"
                    + " is not supported by " + this.getClass());
    }

    /**
     * Returns the key of the HTTP header field at the specified index {@code n}. 
     * The 0th header field may be treated as the HTTP status line, in which case 
     * this method will return {@code null} for {@code n = 0}.
     *
     * <p><b>Usage Example:</b></p>
     * <pre>{@code
     * HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
     * httpConn.setRequestMethod("GET");
     * httpConn.connect();
     *
     * String headerKey = httpConn.getHeaderFieldKey(1);  // Get key of the 1st header field
     * if (headerKey != null) {
     *     System.out.println("Header Key: " + headerKey);
     * } else {
     *     System.out.println("No header key found at index 1.");
     * }
     * }</pre>
     *
     * @param n the index of the header field, where {@code n >= 0}.
     * @return the key for the nth header field, or {@code null} if no key exists 
     *         at the specified index.
     *
     * @see java.net.URLConnection#getHeaderField(int)
     * @see java.net.HttpURLConnection#getHeaderField(int)
     */
    public String getHeaderFieldKey (int n) {
        return null;
    }

    /**
     * Enables streaming of an HTTP request body without internal buffering
     * when the content length is known in advance and does not exceed the maximum 
     * value that can be represented by a 32-bit integer. For larger content lengths, 
     * use {@link #setFixedLengthStreamingMode(long)}.
     * 
     * <p>An exception will be thrown if the application attempts to write more data 
     * than the indicated content length, or if the OutputStream is closed before 
     * the specified amount is written.</p>
     * 
     * <p>When output streaming is enabled, authentication and redirection cannot be 
     * handled automatically. A {@link HttpRetryException} will be thrown if authentication 
     * or redirection is required and the response cannot be read successfully. This 
     * exception provides details about the error encountered.</p>
     * 
     * <p>This method must be called before the URLConnection is connected. Once 
     * connected, the content length cannot be changed.</p>
     * 
     * <p><b>Note:</b> If the content length exceeds the maximum value representable by a 32-bit
     * integer, use {@link #setFixedLengthStreamingMode(long)} to accommodate larger sizes.</p>
     * 
     * @param contentLength the length of the content to be sent, in bytes. This must 
     *                      be a non-negative integer not exceeding the maximum 
     *                      value of a 32-bit signed integer.
     * @throws IllegalStateException if the URLConnection is already connected or if 
     *         a different streaming mode is already enabled.
     * @throws IllegalArgumentException if a content length less than zero is specified.
     * @see #setFixedLengthStreamingMode(long)
     * @see #setChunkedStreamingMode(int)
     * @since 1.5
     */
    public void setFixedLengthStreamingMode (int contentLength) {
        
    }

    /**
     * Enables streaming of an HTTP request body without internal buffering
     * when the content length is known in advance and exceeds the maximum value 
     * that can be represented by a 32-bit integer. This method should be used when the 
     * content length is greater than 2,147,483,647 bytes (2 GB).
     * 
     * <p>An exception will be thrown if the application attempts to write more data 
     * than the indicated content length, or if the OutputStream is closed before 
     * the specified amount is written.</p>
     * 
     * <p>When output streaming is enabled, authentication and redirection cannot be 
     * handled automatically. A {@link HttpRetryException} will be thrown if authentication 
     * or redirection is required and the response cannot be read successfully. This 
     * exception provides details about the error encountered.</p>
     * 
     * <p>This method must be called before the URLConnection is connected. Once 
     * connected, the content length cannot be changed.</p>
     * 
     * <p><b>Note:</b> Use this method for content lengths that exceed the 2 GB limit 
     * of {@link #setFixedLengthStreamingMode(int)}.</p>
     * 
     * @param contentLength the length of the content to be sent, in bytes. This must 
     *                      be a non-negative long value.
     * @throws IllegalStateException if the URLConnection is already connected or if 
     *         a different streaming mode is already enabled.
     * @throws IllegalArgumentException if a content length less than zero is specified.
     * @see #setFixedLengthStreamingMode(int)
     * @see #setChunkedStreamingMode(int)
     * @since 1.7
     */
    public void setFixedLengthStreamingMode(long contentLength) {
        
    }

    /* Default chunk size (including chunk header) if not specified;
    * we want to keep this in sync with the one defined in
    * sun.net.www.http.ChunkedOutputStream
    */
    private static final int DEFAULT_CHUNK_SIZE = 4096;

    /**
     * Enables streaming of an HTTP request body using chunked transfer encoding, 
     * without internal buffering, when the content length is <b>not</b> known in advance.
     * This method sets the chunk size for each chunk of data sent to the server.
     * 
     * <p>Chunked transfer encoding breaks the request body into smaller chunks, 
     * each prefixed with its size in hexadecimal and followed by two CRLFs (carriage return, line feed). 
     * This mode is particularly useful for dynamically generated content or large requests 
     * where the content length cannot be predetermined. However, not all HTTP servers support 
     * chunked transfer encoding.</p>
     * 
     * <p><b>Important Notes:</b></p>
     * <ul>
     *   <li>Chunked streaming mode must be enabled before the connection is established using {@link URLConnection#connect()}.</li>
     *   <li>When this mode is enabled, automatic handling of authentication and redirection is disabled. 
     *   If authentication or redirection is required during the request, a {@link HttpRetryException} will be thrown.</li>
     *   <li>If the specified {@code chunklen} is less than or equal to 5 bytes (including the chunk header), 
     *   a default chunk size of 4096 bytes will be used.</li>
     * </ul>
     * 
     * <p><b>Example Usage:</b></p>
     * <pre>{@code
     * HttpURLConnection httpConn = (HttpURLConnection) new URL("https://example.com").openConnection();
     * httpConn.setChunkedStreamingMode(1024); // Set chunk size to 1024 bytes
     * httpConn.setRequestMethod("POST");
     * httpConn.setDoOutput(true);
     * OutputStream outputStream = httpConn.getOutputStream();
     * // Write request body in chunks
     * }</pre>
     * 
     * <p><b>Default Behavior:</b> If {@code chunklen} is not specified or is invalid, a default chunk size 
     * of 4096 bytes (including the chunk header and payload) will be used.</p>
     * 
     * <p><b>Exceptions:</b></p>
     * <ul>
     *   <li>{@code IllegalStateException} is thrown if the connection has already been established 
     *   or if another streaming mode (such as fixed-length) is enabled.</li>
     * </ul>
     * 
     * @param   chunklen The number of bytes for each chunk, including the chunk size header and payload. 
     *          Must be greater than 5 bytes, otherwise the default value is used.
     * @throws  IllegalStateException if the connection has already been established or if another streaming mode is already set.
     * 
     * @see     #setFixedLengthStreamingMode(int)
     * @see     HttpRetryException
     * @since 1.5
     */
    public void setChunkedStreamingMode (int chunklen) {
        
    }

    /**
     * Returns the value of the HTTP header field at the specified index {@code n}. 
     * This method allows access to HTTP header values returned by the server in 
     * the response. Header fields are indexed starting from 0, where some 
     * implementations may treat the 0th field as the status line (e.g., "HTTP/1.1 200 OK").
     *
     * <p>This method can be used in conjunction with {@link #getHeaderFieldKey(int)} 
     * to iterate over all the headers in the HTTP response. For example, you can 
     * retrieve both the key and value of each header by using the two methods 
     * together in a loop.
     *
     * <p>If the header at the specified index does not exist, this method returns 
     * {@code null}. It also returns {@code null} if the index is out of bounds.
     *
     * <p><b>Usage Example:</b></p>
     * <pre>{@code
     * HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
     * httpConn.setRequestMethod("GET");
     * httpConn.connect();
     * 
     * // Iterate over all headers
     * for (int i = 0;; i++) {
     *     String headerKey = httpConn.getHeaderFieldKey(i);
     *     String headerValue = httpConn.getHeaderField(i);
     *     if (headerKey == null && headerValue == null) {
     *         break; // No more headers
     *     }
     *     System.out.println(headerKey + ": " + headerValue);
     * }
     * }</pre>
     *
     * @param n the index of the header field, where {@code n >= 0}.
     * @return the value of the nth header field, or {@code null} if the header 
     *         does not exist or if the index is out of bounds.
     *
     * @see java.net.HttpURLConnection#getHeaderFieldKey(int)
     * @see java.net.URLConnection#getHeaderField(int)
     */
    @Override
    public String getHeaderField(int n) {
        return null;
    }

    /**
     * An {@code int} representing the three digit HTTP Status-Code.
    * <ul>
    * <li> 1xx: Informational
    * <li> 2xx: Success
    * <li> 3xx: Redirection
    * <li> 4xx: Client Error
    * <li> 5xx: Server Error
    * </ul>
    */
    protected int responseCode = -1;

    /**
     * The HTTP response message.
    */
    protected String responseMessage = null;

    /* static variables */

    /* do we automatically follow redirects? The default is true. */
    private static boolean followRedirects = true;

    /**
     * If {@code true}, the protocol will automatically follow redirects.
    * If {@code false}, the protocol will not automatically follow
    * redirects.
    * <p>
    * This field is set by the {@code setInstanceFollowRedirects}
    * method. Its value is returned by the {@code getInstanceFollowRedirects}
    * method.
    * <p>
    * Its default value is based on the value of the static followRedirects
    * at HttpURLConnection construction time.
    *
    * @see     java.net.HttpURLConnection#setInstanceFollowRedirects(boolean)
    * @see     java.net.HttpURLConnection#getInstanceFollowRedirects()
    * @see     java.net.HttpURLConnection#setFollowRedirects(boolean)
    */
    protected boolean instanceFollowRedirects = followRedirects;

    /* valid HTTP methods */
    private static final String[] methods = {
        "GET", "POST", "HEAD", "OPTIONS", "PUT", "DELETE", "TRACE"
    };

    /**
     * Constructor for the HttpURLConnection.
    * @param u the URL
    */
    protected HttpURLConnection (URL u) {
        super(u);
    }

    /**
     * Sets whether HTTP redirects  (requests with response codes in the 3xx range) should
     * be automatically followed by instances of this class. 
     * 
     * <p>By default, this setting is enabled ({@code true}). When disabled ({@code false}),
     * the connection will return the 3xx status code, and it will be the responsibility of the
     * application to manage redirection.</p>
     * 
     * <p><b>Important Notes:</b></p>
     * <ul>
     *     <li>This setting applies to all {@code HttpURLConnection} instances created after the change.</li>
     *     <li>Applets cannot modify this setting.</li>
     *     <li>If a security manager is present, {@code checkSetFactory()} will be called to ensure the change is allowed. 
     *     A {@link SecurityException} will be thrown if permission is denied.</li>
     * </ul>
     * 
     * <p><b>Example Usage:</b></p>
     * <pre>{@code
     * // Disable automatic redirects
     * HttpURLConnection.setFollowRedirects(false);
     * 
     * HttpURLConnection connection = (HttpURLConnection) new URL("https://example.com").openConnection();
     * connection.connect();
     * 
     * int responseCode = connection.getResponseCode();
     * if (responseCode == HttpURLConnection.HTTP_MOVED_TEMP) {
     *     // Handle the redirection manually
     *     String newLocation = connection.getHeaderField("Location");
     *     System.out.println("Redirected to: " + newLocation);
     * }
     * }</pre>
     * 
     * <p>This method changes the default redirection behavior for all connections. 
     * For controlling redirects on a per-connection basis, use {@link #setInstanceFollowRedirects(boolean)}.</p>
     * 
     * @param set a {@code boolean} specifying whether to follow HTTP redirects. Default is {@code true}.
     * @throws SecurityException if a security manager exists and denies the operation via {@code checkSetFactory()}.
     * 
     * @see SecurityManager#checkSetFactory()
     * @see #getFollowRedirects()
     * @see #setInstanceFollowRedirects(boolean)
     */
    public static void setFollowRedirects(boolean set) {
        @SuppressWarnings("removal")
        SecurityManager sec = System.getSecurityManager();
        if (sec != null) {
            // seems to be the best check here...
            sec.checkSetFactory();
        }
        followRedirects = set;
    }

    /**
     * Returns a {@code boolean} indicating whether HTTP redirects (3xx responses) 
     * should be automatically followed by the current connection. If redirects 
     * are followed, the client will automatically handle 3xx status codes 
     * by making additional requests to the new location provided in the 
     * response's "Location" header.
     *
     * <p>This is a global setting for all HTTP connections and affects 
     * all instances of {@code HttpURLConnection}. The default value is 
     * {@code true}, meaning redirects are automatically followed unless 
     * explicitly disabled.
     *
     * <p><b>Usage Example:</b></p>
     * <pre>{@code
     * boolean followRedirects = HttpURLConnection.getFollowRedirects();
     * if (followRedirects) {
     *     System.out.println("Redirects are automatically followed.");
     * } else {
     *     System.out.println("Redirects are not automatically followed.");
     * }
     * }</pre>
     *
     * @return {@code true} if HTTP redirects are automatically followed, 
     *         {@code false} if they are not.
     *
     * @see java.net.HttpURLConnection#setFollowRedirects(boolean)
     */
    public static boolean getFollowRedirects() {
        return followRedirects;
    }


    /**
     * Configures whether HTTP redirects (requests with response code 3xx) should 
     * be automatically followed by this {@code HttpURLConnection} instance.
     * 
     * <p>When set to {@code true}, this instance will automatically follow HTTP 
     * redirects. When set to {@code false}, the instance will not follow redirects 
     * and the client code must handle redirect responses manually. This setting 
     * affects only the current {@code HttpURLConnection} instance and does not 
     * alter the behavior of other instances or the default behavior of the class.</p>
     * 
     * <p><b>Behavior and Scope:</b></p>
     * <ul>
     *   <li>This method's setting applies only to the current instance of 
     *       {@code HttpURLConnection}. It does not affect other instances of 
     *       {@code HttpURLConnection}, even if they are created from the same 
     *       URL or use the same connection factory.</li>
     *   <li>To affect redirect behavior globally, use the static method 
     *       {@link #setFollowRedirects(boolean)}. This static method sets the 
     *       default redirect-following behavior for all instances of 
     *       {@code HttpURLConnection} created thereafter.</li>
     *   <li>Changing this setting on an instance does not retroactively affect 
     *       connections that were already established or other instances created 
     *       prior to this change.</li>
     * </ul>
     * 
     * <p><b>Usage Example:</b></p>
     * <pre>{@code
     * HttpURLConnection connection = (HttpURLConnection) new URL("https://example.com").openConnection();
     * connection.setInstanceFollowRedirects(false); // Disable automatic redirect following
     * // Handle redirects manually if needed
     * }</pre>
     * 
     * <p><b>Important Notes:</b></p>
     * <ul>
     *   <li>Ensure this method is called before establishing a connection to 
     *       apply the desired redirect behavior.</li>
     *   <li>For global redirect behavior settings affecting all instances, use 
     *       {@link #setFollowRedirects(boolean)}.</li>
     * </ul>
     * 
     * @param followRedirects a {@code boolean} indicating whether HTTP redirects should 
     *        be automatically followed by this instance. {@code true} to follow redirects, 
     *        {@code false} to handle them manually.
     * 
     * @see #getInstanceFollowRedirects()
     * @see #setFollowRedirects(boolean)
     * @since 1.3
     */
    public void setInstanceFollowRedirects(boolean followRedirects) {
        instanceFollowRedirects = followRedirects;
    }

    /**
     * Returns the value of this {@code HttpURLConnection}'s {@code instanceFollowRedirects} field, 
     * which indicates whether this specific connection instance will automatically follow HTTP redirects 
     * (3xx responses). This setting affects only this instance, not the global setting.
     *
     * <p>By default, the value is inherited from the global setting. You can control it for this instance 
     * using {@link #setInstanceFollowRedirects(boolean)}.
     *
     * <p><b>Usage Example:</b></p>
     * <pre>{@code
     * HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
     * boolean followRedirects = httpConn.getInstanceFollowRedirects();
     * System.out.println("Instance follows redirects: " + followRedirects);
     * }</pre>
     *
     * @return {@code true} if this instance will follow redirects automatically, 
     *         {@code false} otherwise.
     *
     * @since 1.3
     * @see java.net.HttpURLConnection#instanceFollowRedirects
     * @see java.net.HttpURLConnection#setInstanceFollowRedirects(boolean)
     */
    public boolean getInstanceFollowRedirects() {
        return instanceFollowRedirects;
    }

    /**
     * Sets the HTTP request method to the specified method (eg. "GET", "POST", "HEAD", "OPTIONS", "PUT", "DELETE", "TRACE").
     * The method must be set before the connection is established by calling {@link #connect()}, or a {@code ProtocolException} will be thrown.
     * 
     * <p>By default, the request method is "GET". This method cannot be called after writing to the output stream or receiving a response from the server.</p>
     * 
     *  <p><b>Allowed HTTP methods:</b></p>
     * <ul>
     *     <li>GET</li>
     *     <li>POST</li>
     *     <li>HEAD</li>
     *     <li>OPTIONS</li>
     *     <li>PUT</li>
     *     <li>DELETE</li>
     *     <li>TRACE</li>
     * </ul>
     * 
     * <p><b>Usage Notes:</b></p>
     * <ul>
     *     <li>This method must be called before the connection is established using {@code connect()} or before writing to the request body (for methods like "POST").</li>
     *     <li>This method is not thread-safe. If multiple threads access the same instance of {@code HttpURLConnection}, external synchronization is required.</li>
     *     <li>Setting the method to "TRACE" requires a special security permission: {@code allowHttpTrace}.</li>
     * </ul>
     * 
     * <p><b>Usage Example:</b></p>
     * <pre>{@code
     * URL url = new URL("https://example.com");
     * HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
     * 
     * // Set the request method to POST
     * httpConn.setRequestMethod("POST");
     * 
     * // Send POST data
     * httpConn.setDoOutput(true);
     * OutputStream os = httpConn.getOutputStream();
     * os.write("param1=value1&param2=value2".getBytes());
     * os.flush();
     * os.close();
     * 
     * // Read the response
     * int responseCode = httpConn.getResponseCode();
     * System.out.println("Response Code: " + responseCode);
     * httpConn.disconnect();
     * }</pre>
     * 
     * @param method the HTTP request method to be set (e.g., "GET", "POST").
     * 
     * @throws ProtocolException if the method cannot be reset after connecting, or if the requested method is invalid for HTTP.
     * @throws IllegalArgumentException if the specified method is null.
     * @throws SecurityException if a security manager is set and the method is "TRACE" but the "allowHttpTrace" {@code NetPermission} is not granted.
     * 
     * @see #getRequestMethod()
     */
    public void setRequestMethod(String method) throws ProtocolException {
    }

    /**
     * Returns the HTTP request method used by this {@code HttpURLConnection} instance. 
     * Common methods include "GET", "POST", "PUT", "DELETE", etc.
     *
     * <p><b>Usage Example:</b></p>
     * <pre>{@code
     * HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
     * String requestMethod = httpConn.getRequestMethod();
     * System.out.println("Request Method: " + requestMethod);
     * }</pre>
     *
     * @return the HTTP request method as a {@code String}.
     *
     * @see java.net.HttpURLConnection#setRequestMethod(String)
     */
    public String getRequestMethod() {
        return method;
    }

    /**
     * Returns the HTTP status code from the response message. For example, 
     * for the following status lines:
     * <ul>
     *   <li>{@code HTTP/1.0 200 OK} returns {@code 200}</li>
     *   <li>{@code HTTP/1.0 401 Unauthorized} returns {@code 401}</li>
     * </ul>
     * If the response is not valid HTTP, or no status code can be discerned, 
     * this method returns {@code -1}.
     *
     * <p><b>Usage Example:</b></p>
     * <pre>{@code
     * HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
     * try {
     *     int responseCode = httpConn.getResponseCode();
     *     System.out.println("Response Code: " + responseCode);
     * } catch (IOException e) {
     *     e.printStackTrace();  // Handle potential connection error
     * }
     * }</pre>
     *
     * @return the HTTP status code, or {@code -1} if no valid status code is found.
     * @throws IOException if an error occurs while connecting to the server.
     *
     * @see java.net.HttpURLConnection#getResponseMessage()
     */
    public int getResponseCode() throws IOException {
        return -1;
    }

    /**
     * Returns the HTTP response message returned by the server, which provides 
     * a textual description of the status code. For example:
     * <ul>
     *   <li>{@code HTTP/1.0 200 OK} returns {@code "OK"}</li>
     *   <li>{@code HTTP/1.0 404 Not Found} returns {@code "Not Found"}</li>
     * </ul>
     * <p>
     * This method should be called after establishing a connection and receiving 
     * a response from the server. It is generally used in conjunction with 
     * {@link #getResponseCode()} to interpret the server's response. The response 
     * code provides the numerical status, while the response message provides 
     * the textual description.
     * </p>
     * <p>
     * If no valid response message can be determined (e.g., if the response is 
     * not a valid HTTP response), this method returns {@code null}.
     * </p>
     * <p><b>Usage Example:</b></p>
     * <pre>{@code
     * HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
     * try {
     *     httpConn.connect(); // Ensure the connection is established
     *     int responseCode = httpConn.getResponseCode();
     *     String responseMessage = httpConn.getResponseMessage();
     *     System.out.println("Response Code: " + responseCode);
     *     System.out.println("Response Message: " + responseMessage);
     * } catch (IOException e) {
     *     e.printStackTrace();  // Handle potential connection error
     * }
     * }</pre>
     * 
     * @return the HTTP response message, or {@code null} if no valid message 
     *         is found.
     * @throws IOException if an error occurs while connecting to the server.
     * 
     * @see #getResponseCode()
     */
    public String getResponseMessage() throws IOException {
        getResponseCode();
        return responseMessage;
    }

    /**
     * Returns the value of the specified header field parsed as a date. The result 
     * is the number of milliseconds since January 1, 1970 GMT. If the field is 
     * missing or cannot be parsed as a date, the provided {@code Default} value 
     * is returned.
     *
     * <p><b>Usage Example:</b></p>
     * <pre>{@code
     * HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
     * long date = httpConn.getHeaderFieldDate("Last-Modified", System.currentTimeMillis());
     * if (date != 0) {
     *     System.out.println("Last-Modified: " + new Date(date));
     * } else {
     *     System.out.println("No valid Last-Modified header.");
     * }
     * }</pre>
     *
     * @param name the name of the header field.
     * @param Default the default value to return if the field is missing or 
     *                malformed.
     * @return the parsed date value in milliseconds since January 1, 1970 GMT, 
     *         or the {@code Default} value if the field is missing or invalid.
     *
     * @see java.net.URLConnection#getHeaderFieldDate(String, long)
     */
    public long getHeaderFieldDate(String name, long defaultValue) {
        return defaultValue;
    }


    /**
     * Terminates the connection to the HTTP server and indicates that no further requests 
     * are expected in the near future. Calling {@code disconnect()} will close the underlying 
     * connection if no other requests are currently sharing the same persistent connection.
     *
     * <p>Once {@code disconnect()} is called, this {@code HttpURLConnection} instance 
     * cannot be reused for additional requests. However, it will not affect other 
     * instances that may be using the same underlying connection, as connection pooling 
     * is managed transparently.
     *
     * <p>This method should be called when the caller is finished using the connection 
     * to release network resources efficiently. It is especially important to call 
     * {@code disconnect()} in environments with limited resources (e.g., mobile devices 
     * or embedded systems).
     * 
     * <p><b>Note:</b> This method is not thread-safe. If multiple threads are using 
     * the same {@code HttpURLConnection}, care must be taken to avoid calling 
     * {@code disconnect()} while other threads are performing network operations on it.
     * 
     * <p><b>Usage Example:</b></p>
     * <pre>{@code
     * URL url = new URL("http://www.example.com");
     * HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
     * try {
     *     httpConn.setRequestMethod("GET");
     *     InputStream responseStream = httpConn.getInputStream();
     *     // Process the response...
     * } finally {
     *     httpConn.disconnect();  // Always disconnect when done
     * }
     * }</pre>
     * 
     * @see java.net.HttpURLConnection#connect()
     * @see java.net.HttpURLConnection#getInputStream()
     * @see java.net.HttpURLConnection#getOutputStream()
     */
    public abstract void disconnect();


    /**
     * Determines whether this {@code HttpURLConnection} is using a proxy.
     * 
     * <p>This method returns {@code true} if the connection is known to be routed through a proxy server, 
     * either via an explicit proxy configuration (e.g., set through system properties such as 
     * {@code http.proxyHost} and {@code http.proxyPort}) or detected automatically. It returns {@code false} 
     * if no proxy is used or if it cannot conclusively determine whether the connection is proxied.</p>
     * 
     * <p><b>Usage Notes:</b></p>
     * <ul>
     *     <li>This method may return {@code false} in cases where the proxy settings are not accessible 
     *     to the current instance or when the proxy is determined dynamically at runtime (e.g., using a 
     *     ProxySelector).</li>
     *     <li>This method only applies to HTTP proxies and may not reflect the use of other proxy types such as SOCKS proxies.</li>
     * </ul>
     * 
     * <p><b>Example Usage:</b></p>
     * <pre>{@code
     * HttpURLConnection connection = (HttpURLConnection) new URL("https://example.com").openConnection();
     * boolean isUsingProxy = connection.usingProxy();
     * if (isUsingProxy) {
     *     System.out.println("The connection is using a proxy.");
     * } else {
     *     System.out.println("The connection is not using a proxy.");
     * }
     * }</pre>
     * 
     * @return {@code true} if the connection is routed through a proxy, {@code false} otherwise or if undetermined.
     * 
     * @see java.net.Proxy
     * @see java.net.ProxySelector
     */
    public abstract boolean usingProxy();

    /**
     * Returns a {@link SocketPermission} object representing the permission required 
     * to connect to the destination host and port. This permission is typically 
     * checked by security managers to determine if the connection is allowed.
     *
     * <p>If an error occurs while determining the permission, an {@link IOException} 
     * is thrown.
     *
     * <p><b>Usage Example:</b></p>
     * <pre>{@code
     * HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
     * try {
     *     Permission permission = httpConn.getPermission();
     *     System.out.println("Permission: " + permission);
     * } catch (IOException e) {
     *     e.printStackTrace();  // Handle potential error
     * }
     * }</pre>
     *
     * @return a {@link SocketPermission} object representing the necessary permission 
     *         to connect to the destination host and port.
     * @throws IOException if an error occurs while computing the permission.
     *
     * @see java.net.SocketPermission
     * @see java.net.URLConnection#getPermission()
     */
    public Permission getPermission() throws IOException {
        return null;
    }

    /**
     * Returns the error stream if the connection failed but the server 
     * still sent useful data. For example, when an HTTP server responds 
     * with a 404 status code (which may cause a {@link FileNotFoundException} 
     * during {@link #connect()}), the server might still send an HTML page 
     * with information or suggestions. This method allows access to such 
     * error data.
     *
     * <p>Calling this method does not initiate a new connection. It only returns 
     * an error stream if the connection has been established and the server 
     * encountered an error while processing the request, but still sent useful 
     * error data. If no error occurred, the server did not send any error data, 
     * or the connection was not made, this method returns {@code null}.
     * 
     * <p><b>Usage Example:</b></p>
     * <pre>{@code
     * HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
     * try {
     *     httpConn.setRequestMethod("GET");
     *     InputStream errorStream = httpConn.getErrorStream();
     *     if (errorStream != null) {
     *         // Process the error stream, e.g., log it or display it to the user
     *     }
     * } finally {
     *     httpConn.disconnect();
     * }
     * }</pre>
     *
     * @return an error stream if the server sent error data, or {@code null} 
     *         if there are no errors, the connection was not established, or the 
     *         server did not provide any error data.
     *
     * @see java.net.HttpURLConnection#getInputStream()
     * @see java.net.HttpURLConnection#connect()
     * @see java.net.HttpURLConnection#disconnect()
     */
    public InputStream getErrorStream() {
        return null;
    }

    /*
    * The response codes for HTTP, as of version 1.1.
    * These codes are used to indicate the result of an HTTP request.
    */

    /* 2XX: Generally successful responses */

    /**
     * HTTP Status-Code 200: OK.
     * <p>
     * The request has succeeded. The meaning of the success depends on the HTTP method:
     * GET: The resource has been fetched and is transmitted in the message body.
     * POST: The resource describing the result of the action is transmitted in the message body.
     * </p>
     */
    public static final int HTTP_OK = 200;

    /**
     * HTTP Status-Code 201: Created.
     * <p>
     * The request has been fulfilled and resulted in a new resource being created.
     * This is typically used in response to a POST request.
     * </p>
     */
    public static final int HTTP_CREATED = 201;

    /**
     * HTTP Status-Code 202: Accepted.
     * <p>
     * The request has been accepted for processing, but the processing has not been completed.
     * </p>
     */
    public static final int HTTP_ACCEPTED = 202;

    /**
     * HTTP Status-Code 203: Non-Authoritative Information.
     * <p>
     * The request was successful but the returned metadata may be incorrect or not up-to-date.
     * </p>
     */
    public static final int HTTP_NOT_AUTHORITATIVE = 203;

    /**
     * HTTP Status-Code 204: No Content.
     * <p>
     * The server successfully processed the request, but is not returning any content.
     * </p>
     */
    public static final int HTTP_NO_CONTENT = 204;

    /**
     * HTTP Status-Code 205: Reset Content.
     * <p>
     * The server successfully processed the request, but the user agent should reset the document view.
     * </p>
     */
    public static final int HTTP_RESET = 205;

    /**
     * HTTP Status-Code 206: Partial Content.
     * <p>
     * The server is delivering only part of the resource due to a range header sent by the client.
     * </p>
     */
    public static final int HTTP_PARTIAL = 206;

    /* 3XX: Relocation/Redirection */

    /**
     * HTTP Status-Code 300: Multiple Choices.
     * <p>
     * The request has more than one possible response. The user agent or user should choose one of them.
     * </p>
     */
    public static final int HTTP_MULT_CHOICE = 300;

    /**
     * HTTP Status-Code 301: Moved Permanently.
     * <p>
     * The requested resource has been assigned a new permanent URI and any future references should use that URI.
     * </p>
     */
    public static final int HTTP_MOVED_PERM = 301;

    /**
     * HTTP Status-Code 302: Found.
     * <p>
     * The resource resides temporarily under a different URI. The client should continue to use the original URI.
     * </p>
     */
    public static final int HTTP_MOVED_TEMP = 302;

    /**
     * HTTP Status-Code 303: See Other.
     * <p>
     * The response to the request can be found under a different URI using a GET method.
     * </p>
     */
    public static final int HTTP_SEE_OTHER = 303;

    /**
     * HTTP Status-Code 304: Not Modified.
     * <p>
     * The resource has not been modified since the last request. The client can use the cached version.
     * </p>
     */
    public static final int HTTP_NOT_MODIFIED = 304;

    /**
     * HTTP Status-Code 305: Use Proxy.
     * <p>
     * The requested resource must be accessed through the proxy given by the Location header.
     * </p>
     */
    public static final int HTTP_USE_PROXY = 305;

    /* 4XX: Client Error */

    /**
     * HTTP Status-Code 400: Bad Request.
     * <p>
     * The server could not understand the request due to invalid syntax.
     * </p>
     */
    public static final int HTTP_BAD_REQUEST = 400;

    /**
     * HTTP Status-Code 401: Unauthorized.
     * <p>
     * Authentication is required and has failed or has not been provided.
     * </p>
     */
    public static final int HTTP_UNAUTHORIZED = 401;

    /**
     * HTTP Status-Code 402: Payment Required.
     * <p>
     * This code is reserved for future use. It is not widely implemented.
     * </p>
     */
    public static final int HTTP_PAYMENT_REQUIRED = 402;

    /**
     * HTTP Status-Code 403: Forbidden.
     * <p>
     * The server understands the request but refuses to authorize it.
     * </p>
     */
    public static final int HTTP_FORBIDDEN = 403;

    /**
     * HTTP Status-Code 404: Not Found.
     * <p>
     * The requested resource could not be found.
     * </p>
     */
    public static final int HTTP_NOT_FOUND = 404;

    /**
     * HTTP Status-Code 405: Method Not Allowed.
     * <p>
     * The request method is known by the server but is not supported by the resource.
     * </p>
     */
    public static final int HTTP_BAD_METHOD = 405;

    /**
     * HTTP Status-Code 406: Not Acceptable.
     * <p>
     * The resource is capable of generating only content that is not acceptable according to the Accept headers sent in the request.
     * </p>
     */
    public static final int HTTP_NOT_ACCEPTABLE = 406;

    /**
     * HTTP Status-Code 407: Proxy Authentication Required.
     * <p>
     * The client must first authenticate itself with the proxy.
     * </p>
     */
    public static final int HTTP_PROXY_AUTH = 407;

    /**
     * HTTP Status-Code 408: Request Timeout.
     * <p>
     * The server timed out waiting for the request.
     * </p>
     */
    public static final int HTTP_CLIENT_TIMEOUT = 408;

    /**
     * HTTP Status-Code 409: Conflict.
     * <p>
     * The request could not be completed due to a conflict with the current state of the resource.
     * </p>
     */
    public static final int HTTP_CONFLICT = 409;

    /**
     * HTTP Status-Code 410: Gone.
     * <p>
     * The requested resource is no longer available and will not be available again.
     * </p>
     */
    public static final int HTTP_GONE = 410;

    /**
     * HTTP Status-Code 411: Length Required.
     * <p>
     * The server refuses to accept the request without a defined Content-Length header.
     * </p>
     */
    public static final int HTTP_LENGTH_REQUIRED = 411;

    /**
     * HTTP Status-Code 412: Precondition Failed.
     * <p>
     * The server does not meet one of the preconditions that the requester put on the request.
     * </p>
     */
    public static final int HTTP_PRECON_FAILED = 412;

    /**
     * HTTP Status-Code 413: Payload Too Large.
     * <p>
     * The request is larger than the server is willing or able to process.
     * </p>
     */
    public static final int HTTP_ENTITY_TOO_LARGE = 413;

    /**
     * HTTP Status-Code 414: URI Too Long.
     * <p>
     * The URI provided was too long for the server to process.
     * </p>
     */
    public static final int HTTP_REQ_TOO_LONG = 414;

    /**
     * HTTP Status-Code 415: Unsupported Media Type.
     * <p>
     * The request entity has a media type which the server or resource does not support.
     * </p>
     */
    public static final int HTTP_UNSUPPORTED_TYPE = 415;

    /* 5XX: Server Error */

    /**
     * HTTP Status-Code 500: Internal Server Error (Deprecated).
     * <p>
     * The server encountered an unexpected condition that prevented it from fulfilling the request.
     * </p>
     * @deprecated This constant is misplaced and should not have existed. Use {@link #HTTP_INTERNAL_ERROR}.
     */
    @Deprecated
    public static final int HTTP_SERVER_ERROR = 500;

    /**
     * HTTP Status-Code 500: Internal Server Error.
     * <p>
     * The server encountered an unexpected condition that prevented it from fulfilling the request.
     * </p>
     */
    public static final int HTTP_INTERNAL_ERROR = 500;

    /**
     * HTTP Status-Code 501: Not Implemented.
     * <p>
     * The server does not support the functionality required to fulfill the request.
     * </p>
     */
    public static final int HTTP_NOT_IMPLEMENTED = 501;

    /**
     * HTTP Status-Code 502: Bad Gateway.
     * <p>
     * The server, while acting as a gateway or proxy, received an invalid response from the upstream server.
     * </p>
     */
    public static final int HTTP_BAD_GATEWAY = 502;

    /**
     * HTTP Status-Code 503: Service Unavailable.
     * <p>
     * The server is currently unable to handle the request due to temporary overloading or maintenance.
     * </p>
     */
    public static final int HTTP_UNAVAILABLE = 503;

    /**
     * HTTP Status-Code 504: Gateway Timeout.
     * <p>
     * The server, while acting as a gateway or proxy, did not receive a timely response from the upstream server.
     * </p>
     */
    public static final int HTTP_GATEWAY_TIMEOUT = 504;

    /**
     * HTTP Status-Code 505: HTTP Version Not Supported.
     * <p>
     * The server does not support the HTTP protocol version that was used in the request.
     * </p>
     */
    public static final int HTTP_VERSION = 505;
}
