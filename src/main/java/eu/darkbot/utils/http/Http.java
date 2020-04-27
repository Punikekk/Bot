package eu.darkbot.utils.http;

import eu.darkbot.utils.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Utility for HTTP connections.
 */
public class Http {
    protected String url;
    protected final Method method;
    protected final boolean followRedirects;

    //Discord doesn't handle java's user agent...
    protected String userAgent = "Mozilla/5.0";
    protected ParamBuilder params;
    protected Map<String, String> headers = new LinkedHashMap<>();

    protected Http(String url, Method method, boolean followRedirects) {
        this.url = url;
        this.method = method;
        this.followRedirects = followRedirects;
    }

    /**
     * Creates new instance of Http with provided url.
     * Request method is {@link Method#GET} and follows redirects by default.
     *
     * @param url to connect
     * @return new Http
     */
    public static Http create(String url) {
        return new Http(url, Method.GET, true);
    }

    /**
     * Creates new instance of Http with provided url and request method.
     * Follows redirects by default.
     *
     * @param url    to connect
     * @param method of request
     * @return new Http
     */
    public static Http create(String url, Method method) {
        return new Http(url, method, true);
    }

    /**
     * Creates new instance of Http with provided url and follow redirects.
     * Request method is {@link Method#GET} by default.
     *
     * @param url             to connect
     * @param followRedirects should follow redirects, response code 3xx
     * @return new Http
     */
    public static Http create(String url, boolean followRedirects) {
        return new Http(url, Method.GET, followRedirects);
    }

    /**
     * Creates new instance of Http with provided arguments.
     *
     * @param url             to connect
     * @param method          of request
     * @param followRedirects should follow redirects, response code 3xx
     * @return new Http
     */
    public static Http create(String url, Method method, boolean followRedirects) {
        return new Http(url, method, followRedirects);
    }

    /**
     * Sets or overrides connection header.
     * Encoded via {@link java.net.URLEncoder#encode(String, String)} in UTF-8
     * To set header without encoding look {@link Http#setRawHeader(String, String)}
     * <p>
     * Equivalent to {@link HttpURLConnection#setRequestProperty(String, String)}
     *
     * @param key   of header
     * @param value of header
     * @return current instance of Http
     */
    public Http setHeader(String key, String value) {
        this.headers.put(ParamBuilder.encode(key), ParamBuilder.encode(value));
        return this;
    }

    /**
     * Sets or overrides connection header without encoding.
     * Equivalent to {@link HttpURLConnection#setRequestProperty(String, String)}
     *
     * @param key   of header
     * @param value of header
     * @return current instance of Http
     */
    public Http setRawHeader(String key, String value) {
        this.headers.put(key, value);
        return this;
    }

    /**
     * Sets or overrides parameter for POST as body or for GET as
     * additional query url only if current url doesn't contains '?' char.
     * Is encoded via {@link java.net.URLEncoder#encode(String, String)}
     *
     * @param key   of parameter
     * @param value of parameter
     * @return current instance of Http
     */
    public Http setParam(Object key, Object value) {
        if (this.params == null)
            this.params = ParamBuilder.create(ParamBuilder.encode(key), ParamBuilder.encode(value));
        else this.params.set(key, value);
        return this;
    }

    /**
     * Sets or overrides parameter for POST as body or for GET as
     * additional query url only if current url doesn't contains '?' char.
     * Be aware, this wont be encoded via {@link java.net.URLEncoder#encode(String, String)}
     *
     * @param key   of parameter
     * @param value of parameter
     * @return current instance of Http
     */
    public Http setRawParam(Object key, Object value) {
        if (this.params == null)
            this.params = ParamBuilder.create(key, value);
        else this.params.setRaw(key, value);
        return this;
    }

    /**
     * Sets user agent used in connection.
     * Default is "Mozilla/5.0".
     *
     * @param userAgent to use.
     * @return current instance of Http
     */
    public Http setUserAgent(String userAgent) {
        this.userAgent = userAgent;
        return this;
    }

    /**
     * Connects, gets and converts InputStream to String.
     * TODO: creates new connection on each call
     *
     * @return body of request as String
     * @throws IOException of {@link IOUtils#read(InputStream)}
     */
    public String getContent() throws IOException {
        return IOUtils.read(getInputStream());
    }

    /**
     * Gets and closes InputStream of current connection.
     * TODO: creates new connection on each call
     *
     * @throws IOException of {@link Http#getInputStream()}
     */
    public void closeInputStream() throws IOException {
        getInputStream().close();
    }

    /**
     * Gets InputStream of current connection.
     * TODO: creates new connection on each call
     *
     * @return InputStream of connection
     * @throws IOException of {@link Http#getConnection()}
     */
    public InputStream getInputStream() throws IOException {
        return getConnection().getInputStream();
    }

    /**
     * Gets {@link HttpURLConnection} with provided params,
     * request method, and body.
     * TODO: creates new connection on each call
     *
     * @return HttpURLConnection
     * @throws IOException of connection
     */
    public HttpURLConnection getConnection() throws IOException {
        if (method == Method.GET && params != null && !url.contains("?"))
            url += "?" + params.toString();

        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setInstanceFollowRedirects(followRedirects);
        conn.setRequestProperty("User-Agent", userAgent);
        if (!headers.isEmpty()) headers.forEach(conn::setRequestProperty);

        if (method == Method.POST && params != null) {
            byte[] data = params.getBytes();
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Length", String.valueOf(data.length));

            conn.getOutputStream().write(data);
            conn.getOutputStream().flush();
            conn.getOutputStream().close();
        }

        return conn;
    }
}