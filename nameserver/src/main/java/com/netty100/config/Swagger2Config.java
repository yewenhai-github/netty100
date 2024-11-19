package com.netty100.config;

import com.github.xiaoymin.swaggerbootstrapui.annotations.EnableSwaggerBootstrapUI;
import com.google.common.base.Predicates;
import com.netty100.utils.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

/**
 * @author why
 */
@Configuration
@EnableSwagger2
@Profile("dev")
@EnableSwaggerBootstrapUI
@SuppressWarnings("ALL")
public class Swagger2Config implements WebMvcConfigurer {
    /**
     * 启用
     */
    @Value("${swagger.enable}")
    private boolean enable;


    @Bean
    public Docket createRestApi() {
        List<Parameter> pars = new ArrayList<>();
        ParameterBuilder clientPar = new ParameterBuilder();
        clientPar.name(Jwts.HEADER).description("登录token").modelRef(new ModelRef("string"))
                .parameterType("header")
                .defaultValue("50572b3a8d8f46c8539a7cda929f32fa68d34009bc5bb7b3586f2c7d18613264ac5e183d7f5a92bf81a680754c93a78114fabc18425d7e522763af6e3c4ba910a11e7bab86b39cf7b082ee1e1f4e0943cdadf30440b51aedc61adfc88ec84974e8938e9acf2e050e8d0843a5b68df2cc28827a99ba66e3d6058d09769ae14a837645059766159662ec9e66407cf59c953cda33ea3b041cd6b5e001acf963dc85f4e94d0931aba1a8fc2ba90e1a00d368b5280e1d8c9c6e5375ee359a29f5c5c021fe67a3457a4434b7786eec3ac0dd28cf19177df9cf335a216ef8636f823057b5ec918aefdb2ec192fcb011d9b3400a540a226dc4f0d472e4bef13ef84812f409cc7dd9bad983980f2d736fa934c4a447681dbccc09c9a8cc2a96a7836320e8").required(false).build();
        pars.add(clientPar.build());
        return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo()).enable(enable).select()
                .apis(RequestHandlerSelectors.any())
                .paths(Predicates.not(PathSelectors.regex("/error.*")))
                .paths(input -> PathSelectors.regex("/*.*").apply(input))
                .build().globalOperationParameters(pars);
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("swagger接口文档").description("swagger接口文档").version("1.0").build();
    }

    @Bean
    public ViewResolver viewResolver() {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setViewClass(JstlView.class);
        resolver.setPrefix("/");
        resolver.setSuffix(".html");
        return resolver;

    }

    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        return messageSource;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }


}