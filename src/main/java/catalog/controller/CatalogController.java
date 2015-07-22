package catalog.controller;

import catalog.service.PaginationService;
import catalog.service.XmlService;
import java.io.IOException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class CatalogController {    
    
    @Autowired
    PaginationService pagination;
    
    @Autowired
    XmlService xmlService;
    
    private final int PAGE_SIZE = 2;
    
    private String getRealPath(HttpServletRequest request) {
        ServletContext context = request.getServletContext();
        String realPath = context.getRealPath("");
        
        return realPath;
    }
    
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView index(HttpServletRequest request) {
        return index(request, 1);
    }
    
    
    @RequestMapping(value = "/page/{page}", method = RequestMethod.GET)
    public ModelAndView index(
            HttpServletRequest request,
            @PathVariable("page") Integer page) {
        
        ModelAndView model = new ModelAndView("index"); 
        
        PagedListHolder list = new PagedListHolder(xmlService.getList(getRealPath(request)));
        
        if (page < 1) {
            page = 1;
        }
        
        list.setPage(--page);
        list.setPageSize(PAGE_SIZE);
        
        model.addObject("list", list);        
        model.addObject("contextPath", request.getContextPath());
        
        return model;
    }
    
    @RequestMapping(value = "/download", method = RequestMethod.GET)
    public ModelAndView download(HttpServletRequest request) {
        
        ModelAndView model = new ModelAndView("download");
        model.addObject("contextPath", request.getContextPath());        
        
        return model;
    }
     
    /**
     * Method for handling file download request from client
     */
    @RequestMapping(value = "/download-file", method = RequestMethod.GET)
    public void downloadFile(HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        xmlService.download(request, response);
    }
    
    @RequestMapping(value = "/upload", method = RequestMethod.GET)
    public ModelAndView upload(HttpServletRequest request) {
        
        ModelAndView model = new ModelAndView("upload");
        model.addObject("contextPath", request.getContextPath());        
        
        return model;
    }
    
    @RequestMapping(value = "/upload-file", method = RequestMethod.POST)
    public ModelAndView uploadFile(HttpServletRequest request,
            @RequestParam("file") MultipartFile file) {
         
        ModelAndView model = new ModelAndView("upload-file");
        
        model.addObject("contextPath", request.getContextPath());
        
        model.addObject("result", xmlService.upload(request, file));
       
        return model;
    }
    
}
