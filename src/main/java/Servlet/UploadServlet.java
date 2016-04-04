package Servlet;

import GlobalLogicTask.Unzip;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class UploadServlet extends HttpServlet {

    private boolean isMultipart;
    private String filePath;
    private File file ;

    public void init( ){
        // Get the file location where it would be stored.
        filePath = getServletContext().getInitParameter("file-upload");
    }

    public void doPost(HttpServletRequest request,
                       HttpServletResponse response)
            throws ServletException, java.io.IOException {

        isMultipart = ServletFileUpload.isMultipartContent(request);
        response.setContentType("text/html; charset=UTF-8");

        PrintWriter out = new PrintWriter(new OutputStreamWriter(response.getOutputStream(), StandardCharsets.UTF_8), true);
        if( !isMultipart ){
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet upload</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<p>No file uploaded</p>");
            out.println("</body>");
            out.println("</html>");
            return;
        }

        // Create a new file upload handler
        ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory());

        try{
            // Parse the request to get file items
            List fileItems = upload.parseRequest(request);

            // Process the uploaded file items
            Iterator i = fileItems.iterator();

            out.println("<html>");
            out.println("<head>");
            out.println("<meta charset=\"utf-8\">");
            out.println("<title>Servlet upload</title>");
            out.println("</head>");
            out.println("<body>");

            while ( i.hasNext () )
            {
                FileItem fi = (FileItem)i.next();
                if ( !fi.isFormField () )
                {
                    String fileName = fi.getName();
                    // Write the file
                    if (fileName.lastIndexOf("\\") >= 0) {
                        file = new File(filePath +
                                fileName.substring(fileName.lastIndexOf("\\")));
                    } else {
                        file = new File(filePath +
                                fileName.substring(fileName.lastIndexOf("\\") + 1));
                    }

                    fi.write( file );
                    out.println("Uploaded Filename: " + fileName + "<br>");
                    Unzip unzip = new Unzip(file.getPath());
                    Map<String, String> productsMap = unzip.execUnzip();

                    char capitalFirstLetter = ' ';
                    out.println("<ul style=\"list-style-type: none;\">");
                    for (Map.Entry<String, String> values : productsMap.entrySet()) {
                        char productFirstLetter = Character.toUpperCase(values.getKey().charAt(0));
                        out.println("<li>");

                        if (capitalFirstLetter != productFirstLetter) {
                            capitalFirstLetter = productFirstLetter;
                            out.println(capitalFirstLetter + " : " + values.getKey());
                        } else {
                            out.println("&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp" + values.getKey());
                        }
                        out.println("</li>");
                    }
                    out.println("</ul>");
                }
            }
            out.println("</body>");
            out.println("</html>");
        }catch(Exception ex) {
            System.out.println(ex);
        }
    }

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response)
            throws ServletException, java.io.IOException {

        request.getRequestDispatcher("mypage.jsp").forward(request, response);

    }

}
